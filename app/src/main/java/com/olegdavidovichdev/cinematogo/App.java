package com.olegdavidovichdev.cinematogo;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.olegdavidovichdev.cinematogo.model.ConfigurationResponse;
import com.olegdavidovichdev.cinematogo.rest.ApiClient;
import com.olegdavidovichdev.cinematogo.rest.ApiInterface;
import com.olegdavidovichdev.cinematogo.service.CheckConfigurationService;
import com.orm.SugarContext;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Oleg on 28.11.2016.
 */

public class App extends Application {

    private static final String TASK_TAG = "periodicTask";
    private static final long DEFAULT_PERIOD = 10800;
    private static final long DEFAULT_FLEX = 100;
    private static String baseUrlImages;

    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        if (!prefs.getBoolean("firstTime", false)) {

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ConfigurationResponse> checkConfiguration = apiService.getConfiguration("e755e32ddf1ac688f8617a68c325d41d");
            checkConfiguration.enqueue(new Callback<ConfigurationResponse>() {
                @Override
                public void onResponse(Call<ConfigurationResponse> call, Response<ConfigurationResponse> response) {
                    baseUrlImages = response.body().getImages().getBaseUrl();

                    // add to preferences basic image url
                    SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor e = myPrefs.edit();
                    e.putString("baseUrlImages", baseUrlImages);
                    e.apply();

                    Log.d("MainActivity", myPrefs.getAll().toString());

                    Log.d("MainActivity", "Loaded! BaseUrlImages = " + baseUrlImages);
                    Toast.makeText(getApplicationContext(), "App get new API configuration", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ConfigurationResponse> call, Throwable t) {
                    Log.d("MainActivity", "Fail to download BaseUrlImages");
                    Toast.makeText(getApplicationContext(), "Fail to download BaseUrlImages", Toast.LENGTH_LONG).show();
                }
            });

            CheckConfigurationService.periodicSync(this, DEFAULT_PERIOD, DEFAULT_FLEX, TASK_TAG, false, true);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();

            Log.d("MainActivity", "Hello from App");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }


}


