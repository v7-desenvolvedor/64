package ru.edgar.launcher.fragment;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.edgar.launcher.activity.MainActivity;
import ru.edgar.launcher.model.Api;
import ru.edgar.launcher.model.FaqList;
import ru.edgar.launcher.model.Main;
import ru.edgar.launcher.model.News;
import ru.edgar.launcher.model.Servers;
import ru.edgar.launcher.model.edgar;
import ru.edgar.launcher.other.Interface;
import ru.edgar.launcher.other.Lists;
import ru.edgar.space.R;

public class SplashFragment extends MainActivity{

    ImageView splash_logo;

    public ee panzto;

    String apiLink;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public SplashFragment() {
        super();
        splashInit();
    }

    public void splashInit() {
        if(viewGroup != null) {
            return;
        }
        viewGroup = (ViewGroup) ((LayoutInflater) MainActivity.getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_splash, (ViewGroup) null);
        MainActivity.getMainActivity().front_ui_layout.addView(viewGroup, -1, -1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        viewGroup.setLayoutParams(layoutParams);
        splash_logo = (ImageView) viewGroup.findViewById(R.id.splash_logo);

        Lists.faqlist = new ArrayList<>();
        Lists.slist = new ArrayList<>();
        Lists.nlist = new ArrayList<>();

        loadJsons();

        viewGroup.setVisibility(View.GONE);
    }

    public class loadJsonRepit implements View.OnClickListener {
        public loadJsonRepit() {
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            loadJsons();
            MainActivity.getMainActivity().dialogFragment.hide();
        }
    }

    public class noUpdate implements View.OnClickListener {

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            MainActivity.getMainActivity().dialogFragment.hide();
            loadJsons();
        }
    }

    public class downloadApk implements View.OnClickListener {
        public String[] launcher = new String[3];

        public downloadApk(String url, String path, String name) {
            launcher[0] = url;
            launcher[1] = path;
            launcher[2] = name;
        }

        @Override // android.view.View.OnClickListener
        public final void onClick(View view) {
            MainActivity.getMainActivity().dialogFragment.hide();
            MainActivity.getMainActivity().splashFragment.hide();
            MainActivity.getMainActivity().downloadFragment.startDownloadApk(launcher[0], launcher[1], launcher[2]);
        }
    }

    public void loadJsons() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api-free.edgars.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Interface sInterface = retrofit.create(Interface.class);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1) // 3600 (published)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(MainActivity.getMainActivity(), new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    apiLink = mFirebaseRemoteConfig.getString("api1Link");
                } else {
                    Log.e("Google FireBase", "SLIHILAC HOPA");
                }

                sInterface.getApi(apiLink).enqueue(new Callback<Api>() {
                    public void onResponse(Call<Api> call, Response<Api> response) {
                        if(response.isSuccessful())
                        {
                            if(response.body() != null) {             
                    
                                    Lists.archives.clear();
                                    Lists.archives.addAll(response.body().getArchives());
                                    Lists.deleted.clear();
                                    Lists.deleted.addAll(response.body().getDeleted());

                                    Lists.launcher_dan = new String[]{response.body().getLauncherUrl(), response.body().getLauncherPath(), response.body().getLauncherName()};

                                    sInterface.getMain(response.body().getApi()).enqueue(new Callback<Main>() {
                                        @Override
                                        public void onResponse(Call<Main> call, Response<Main> response) {

                                            String storiesLink = response.body().getStories();

                                            String faqLink = response.body().getFaq();

                                            Lists.createCharacterUrl = response.body().getCreateCharacter();

                                            Lists.verifyAuthUrl = response.body().getVerifyAuth();

                                            Lists.accountDetailsUrl = response.body().getAccountDetails();

                                            Lists.isAccUrl = response.body().getIsAcc();

                                            Lists.skinsCDNUrl = response.body().getSkinsCDN();

                                            sInterface.getServers(response.body().getServers()).enqueue(new Callback<List<Servers>>() {
                                                @Override
                                                public void onResponse(Call<List<Servers>> call, Response<List<Servers>> response) {

                                                    List<Servers> servers = response.body();
                                                    for (Servers server : servers) {
                                                        Lists.slist.add(new Servers(server.getName(), server.getColor(), server.getStatus(), server.getRecommend(), server.getNewStatus(), server.getEdgarHost(), server.getEdgarPort(), server.getId()));
                                                    }

                                                    ArrayList<Servers> serversItem = Lists.slist;
                                                    ArrayList<Servers> serversrec = new ArrayList<>();
                                                    ArrayList<Servers> serversnew = new ArrayList<>();
                                                    ArrayList<Servers> serversbce = new ArrayList<>();
                                                    ArrayList<Servers> serverss = new ArrayList<>();

                                                    //Log.e("edgar", "serversItem.size() = " + serversItem.size());

                                                    boolean s = false;
                                                    boolean n = false;
                                                    int i;

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        //Log.e("edgar", "1 id = " + i);
                                                        if (!serversss.getRecommend()) {
                                                            //Log.e("edgar", "1 id = " + i);
                                                            //Log.e("edgar", "recommend = false");
                                                            serversbce.add(serversss);
                                                        }
                                                        //serversItem.remove(i);
                                                    }

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        //Log.e("edgar", "2 id = " + i);
                                                        if (!serversss.getNewStatus() && serversss.getRecommend()) {
                                                            if (!s) {
                                                                //Log.e("edgar", "2 id = " + i);
                                                                //Log.e("edgar", "recommend = true, NewStatus = false (rec)");
                                                                serversrec.add(serversss);
                                                                serversItem.remove(i);
                                                                s = true;
                                                                i--;
                                                            } else {
                                                                serversbce.add(serversss);
                                                                //Log.e("edgar", "recommend = false");
                                                            }
                                                        }
                                                    }

                                                    for (i = 0; i < serversItem.size(); i++) {
                                                        Servers serversss = serversItem.get(i);
                                                        //Log.e("edgar", "3 id = " + i);
                                                        if (serversss.getNewStatus() && serversss.getRecommend()) {
                                                            if (!n) {
                                                                //Log.e("edgar", "3 id = " + i);
                                                                //Log.e("edgar", "recommend = true, NewStatus = true (new)");
                                                                serversnew.add(serversss);
                                                                serversItem.remove(i);
                                                                n = true;
                                                                i--;
                                                            } else {
                                                                serversbce.add(serversss);
                                                                //Log.e("edgar", "recommend = false");
                                                            }
                                                        }
                                                    }
                                                    if (serversrec.size() >= 1) {
                                                        serverss.addAll(serversrec);
                                                        //Log.e("edgar", "serversrec.size() > " + serversrec.size());

                                                    }
                                                    if (serversnew.size() >= 1) {
                                                        serverss.addAll(serversnew);
                                                    }
                                                    serverss.addAll(serversbce);
                                                    //Log.e("edgar", "serversItem.3 > " + serverss.size());
                                                    Lists.slist = serverss;

                                                    sInterface.getStories(storiesLink).enqueue(new Callback<List<News>>() {
                                                        @Override
                                                        public void onResponse(Call<List<News>> call, Response<List<News>> response) {

                                                            List<News> news = response.body();

                                                            for (News storie : news) {
                                                                Lists.nlist.add(new News(storie.getImageUrl(), storie.getTitle(), storie.getTitleBig(), storie.getUrl(), storie.getImageFullUrl()));
                                                            }

                                                            sInterface.getFaqList(faqLink).enqueue(new Callback<FaqList>() {
                                                                public void onFailure(Call<FaqList> call, Throwable th) {
                                                                }

                                                                public void onResponse(Call<FaqList> call, Response<FaqList> response) {
                                                                    if (response.body() != null) {
                                                                        Lists.faqlist.clear();
                                                                        Lists.faqlist.addAll(response.body().getArray());
                                                                    }

                                                                    sApi = true;
                                                                    if (sApi && testApi/* && isPermissions*/) {
                                                                        hide();
                                                                        //
                                                                        MainActivity.getMainActivity().mainFragment.upServerId();
                                                                        //
                                                                        mHandler.postDelayed(new MainOpen(), 300L);
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onFailure(Call<List<News>> call, Throwable t) {
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(Call<List<Servers>> call, Throwable t) {
                                                }
                                            });

                                        }

                                        @Override
                                        public void onFailure(Call<Main> call, Throwable t) {
                                        }
                                    });
                                
                            } else {
                                Log.e("api-", "api----");
                                MainActivity.getMainActivity().openDialog(R.drawable.ic_launcher_alert, "Не удаётся установить соединение с сервером!\nПовторите попытку позже.", "Повторить", null, new loadJsonRepit(), null);
                            }
                        } else {
                            Log.e("api-", "api---1-");
                            MainActivity.getMainActivity().openDialog(R.drawable.ic_launcher_alert, "Не удаётся установить соединение с сервером!\nПовторите попытку позже.", "Повторить", null, new loadJsonRepit(), null);
                        }
                    }
                    public void onFailure(Call<Api> call, Throwable th) {
                        Log.e("api-", "api----" + th.toString());
                        MainActivity.getMainActivity().openDialog(R.drawable.ic_launcher_alert, "Не удаётся установить соединение с сервером!\nПовторите попытку позже.", "Повторить", null, new loadJsonRepit(), null);
                    }
                });
            }
        });
    }

    public void show() {
        viewGroup.clearAnimation();
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.setAlpha(1.0f);
        viewGroup.animate().setDuration(0L).start();
        m();
    }

    public void hide() {
        ee eVar = this.panzto;
        if (eVar != null) {
            eVar.eeB = null;
            eVar.eeC = null;
            eVar.a();
            this.panzto = null;
        }
        splash_logo.clearAnimation();
        splash_logo.setScaleX(1.0f);
        splash_logo.setScaleY(1.0f);
        splash_logo.setTranslationY(0.0f);
        splash_logo.animate().setDuration(150L).scaleX(0.0f).scaleY(0.0f).translationY(this.splash_logo.getHeight()).start();
        viewGroup.clearAnimation();
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.setAlpha(1.0f);
        viewGroup.animate().alpha(0.0f).setDuration(150L).start();
    }

    public class onDes implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            MainActivity.getMainActivity().dialogFragment.hide();
            MainActivity.getMainActivity().onDestroy();
            loadJsons();
        }
    }

    public static final class ee {

        public float mFloat1;
        public float mFloat;
        public long mLong;
        public float mFloat2;
        public long Long;
        public boolean mBool;
        public c eeC;
        public b eeB;
        public TimeInterpolator mTimeInterpolator;
        public Handler mHandler = new Handler();

        public class EEE implements Runnable {
            public EEE() {
            }

            public final void run() {
                ee.this.e();
            }
        }

        public interface b {
            void a();

            void b();
        }

        public interface c {
            void a(ee eVar);
        }

        public ee(float f10, float f11) {
            this.mFloat1 = f10;
            this.mFloat = f11;
            this.mLong = 1000;
            this.mFloat2 = 0.0f;
            this.mBool = false;
            this.mTimeInterpolator = new LinearInterpolator();
        }

        public final void a() {
            if (this.mBool) {
                this.mBool = false;
                this.mHandler.removeCallbacksAndMessages((Object) null);
                b bVar = this.eeB;
                if (bVar != null) {
                    bVar.a();
                }
            }
        }

        public final float b() {
            float f10 = this.mFloat1;
            return e(this.mFloat, this.mFloat1, this.mTimeInterpolator.getInterpolation(this.mFloat2), f10);
        }
        public static float e(float f10, float f11, float f12, float f13) {
            return ((f10 - f11) * f12) + f13;
        }

        public final void c() {
            b bVar = this.eeB;
            if (bVar != null) {
                bVar.a();
            }
        }

        public final void d() {
            if (!this.mBool) {
                this.Long = System.currentTimeMillis();
                this.mBool = true;
                b bVar = this.eeB;
                if (bVar != null) {
                    bVar.b();
                }
                e();
            }
        }

        public final void e() {
            if (this.mBool) {
                float currentTimeMillis = ((float) (System.currentTimeMillis() - this.Long)) / ((float) this.mLong);
                if (currentTimeMillis >= 1.0f) {
                    this.mFloat2 = 1.0f;
                    this.mBool = false;
                } else {
                    this.mFloat2 = currentTimeMillis;
                    this.mHandler.post(new EEE());
                }
                c cVar = this.eeC;
                if (cVar != null) {
                    cVar.a(this);
                }
                if (!this.mBool) {
                    c();
                }
            }
        }
    }

    //

    public class d implements ee.c {
        public d() {
        }

        public final void a(ee eVar) {
            float f11;
            float floatValue = Float.valueOf(eVar.b()).floatValue();
            float f10 = (floatValue - 1.3f) + 1.0f;
            if (floatValue <= 1.0f) {
                splash_logo.setScaleX(1.0f);
                splash_logo.setScaleY(1.0f);
                return;
            }
            if (floatValue <= 1.0f || floatValue > 1.15f) {
                if (floatValue > 1.15f && floatValue <= 1.3f) {
                    f10 = 1.15f - (floatValue - 1.15f);
                    splash_logo.setScaleX(f10);
                } else if (floatValue > 1.3f && floatValue <= 1.45f) {
                    f11 = (floatValue - 1.3f) + 1.0f;
                    //f10 = 1.15f - (floatValue - 1.3f);
                    splash_logo.setScaleX(f11);
                } else if (floatValue > 1.45f && floatValue <= 1.6f) {
                    f10 = 1.15f - (floatValue - 1.45f);
                    splash_logo.setScaleX(f10);
                } else {
                    return;
                }
                splash_logo.setScaleY(f10);
                return;
            }
            f11 = (floatValue - 1.0f) + 1.0f;
            splash_logo.setScaleX(f11);
            splash_logo.setScaleY(f11);
        }
    }

    public class e implements ee.b {
        public e() {
        }

        public final void a() {
            splash_logo.setScaleX(1.0f);
            splash_logo.setScaleY(1.0f);
            m();
        }

        public final void b() {
        }
    }

    public final void m() {
        ee eVar = this.panzto;
        if (eVar != null) {
            eVar.eeB = null;
            eVar.eeC = null;
            eVar.a();
            this.panzto = null;
        }
        ee eVar2 = new ee(0.0f, 1.6f);
        this.panzto = eVar2;
        eVar2.eeC = new d();
        eVar2.eeB = new e();
        eVar2.mTimeInterpolator = new LinearInterpolator();
        ee eVar3 = this.panzto;
        eVar3.mLong = 1600;
        eVar3.d();
    }

}
