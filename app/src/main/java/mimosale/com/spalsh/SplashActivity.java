package mimosale.com.spalsh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import mimosale.com.R;
import mimosale.com.helperClass.CustomUtils;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.HomeActivity;
import mimosale.com.home.preferences.PreferenceListModel;
import mimosale.com.home.preferences.PreferencesListAdapter;
import mimosale.com.login.LoginActivity;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import mimosale.com.no_internet.NoInternetActivity;
import mimosale.com.settings.ChangeLanguageActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static mimosale.com.helperClass.PrefManager.IS_LOGIN;
import static mimosale.com.helperClass.PrefManager.getInstance;

public class SplashActivity extends AppCompatActivity {
    Context context;
    PrefManager manager;
    Locale LOCALE;
    Configuration CONFIG;
    private static int SPLASH_TIME_OUT = 5000;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;
        manager=  PrefManager.getInstance(SplashActivity.this);
        String language = PrefManager.getInstance(SplashActivity.this).getAppLanguageCode();

        if(language.isEmpty())
            language= CustomUtils.JAPNEESE_CODE;

        String languageToLoad  =language; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        if (PrefManager.getInstance(SplashActivity.this).getLanguage().equals("2"))
        {
            manager.setLanguage(CustomUtils.JAPAN);
            manager.setAppLanguageCode(CustomUtils.JAPNEESE_CODE);
            LOCALE = new Locale(CustomUtils.JAPAN);
            Locale.setDefault(LOCALE);
            CONFIG = new Configuration();
            CONFIG.locale = LOCALE;
            CONFIG.setLocale(LOCALE);
            getApplicationContext().createConfigurationContext(CONFIG);
            getResources().updateConfiguration(CONFIG, getResources().getDisplayMetrics());
            onConfigurationChanged(CONFIG);
        }
        else
        {
            manager.setLanguage(CustomUtils.ENGLISH);
            manager.setAppLanguageCode(CustomUtils.ENGLISH_CODE);
            LOCALE = new Locale(CustomUtils.ENGLISH);
            Locale.setDefault(LOCALE);
            CONFIG = new Configuration();
            CONFIG.locale = LOCALE;
            CONFIG.setLocale(LOCALE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getApplicationContext().createConfigurationContext(CONFIG);
            }
            getResources().updateConfiguration(CONFIG, getResources().getDisplayMetrics());
            onConfigurationChanged(CONFIG);
            PrefManager.getInstance(SplashActivity.this).setLanguage("1");
        }


       // getBannerImage();

          new Handler().postDelayed(new Runnable() {



                @Override
                public void run() {
                    if (CustomUtils.isNetworkAvailable(SplashActivity.this)) {
                        PrefManager.getInstance(SplashActivity.this);

                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();


                    } else {
                        // startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
                        Intent i = new Intent(SplashActivity.this, NoInternetActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, SPLASH_TIME_OUT);



    }

    public void getBannerImage()
    {

        try{

            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.GetBannerImages( new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status=jsonObject.getString("status");

                        if (status.equals("1"))
                        {
                            JSONArray data=jsonObject.getJSONArray("data");
                            PrefManager.getInstance(SplashActivity.this).setBanner(data.toString());
                            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();




                        }



                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(SplashActivity.this ,getResources().getString(R.string.check_internet), Toast.LENGTH_LONG ).show();
                    Log.i("fdfdfdfdfdf",""+error.getMessage());

                }
            });
        }
        catch ( Exception e){
            e.printStackTrace();

        }


    }//userPreferencesClose
}
