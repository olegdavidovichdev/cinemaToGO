package com.olegdavidovichdev.cinematogo;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.olegdavidovichdev.cinematogo.activity.MainActivity;
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

public class CinemaToGoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}

