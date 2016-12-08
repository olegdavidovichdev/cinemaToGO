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

public class CinemaToGoApplication extends Application {

    private static final String TAG = CinemaToGoApplication.class.getSimpleName();
    private static final String TASK_TAG = "periodicTask";
    private static final long DEFAULT_PERIOD = 10800;
    private static final long DEFAULT_FLEX = 100;
    private static String baseUrlImages;

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

