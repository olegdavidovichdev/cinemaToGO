package com.olegdavidovichdev.cinematogo.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;
import com.olegdavidovichdev.cinematogo.model.ConfigurationResponse;
import com.olegdavidovichdev.cinematogo.rest.ApiClient;
import com.olegdavidovichdev.cinematogo.rest.ApiInterface;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Oleg on 27.11.2016.
 */

public class CheckConfigurationService extends GcmTaskService
{
    private SharedPreferences sp;
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String APP_PREFERENCES_BASE_URL_IMAGES = "baseUrlImages";
    private static final String EXTRA_KEY_UPDATE = "update";
    private static boolean notify;

    @Override
    public int onRunTask(TaskParams taskParams)
    {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ConfigurationResponse> checkConfiguration = apiService.getConfiguration("e755e32ddf1ac688f8617a68c325d41d");
        checkConfiguration.enqueue(new Callback<ConfigurationResponse>() {
            @Override
            public void onResponse(Call<ConfigurationResponse> call, Response<ConfigurationResponse> response) {

                SharedPreferences sp = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                e.putString(APP_PREFERENCES_BASE_URL_IMAGES, response.body().getImages().getBaseUrl());
                e.apply();
                Log.d("MainActivity", "Url is updated = " + sp.getAll().toString());
                if (notify) createNotification();
            }

            @Override
            public void onFailure(Call<ConfigurationResponse> call, Throwable t) {
                Log.d("MainActivity", "Failed to download API configuration");
            }
        });
        return GcmNetworkManager.RESULT_SUCCESS;
    }



    public static void periodicSync(Context ctx, long period, long flex, String tag, boolean currentUpdate, boolean notif) {

        PeriodicTask periodicTask = new PeriodicTask.Builder()
                .setService(CheckConfigurationService.class)
                .setTag(tag)
                .setPeriod(period)
                .setFlex(flex)
                .setRequiredNetwork(Task.NETWORK_STATE_ANY)
                .setUpdateCurrent(currentUpdate)
                .setPersisted(true)
                .build();

        GcmNetworkManager gnm = GcmNetworkManager.getInstance(ctx);
        gnm.schedule(periodicTask);

        setNotify(notif);
    }


    public static void setNotify(boolean notify) {
        CheckConfigurationService.notify = notify;
    }

    private void createNotification() {

       // Toast.makeText(getApplicationContext(), "API configuration was successfully updated", Toast.LENGTH_LONG).show();
        Calendar now = Calendar.getInstance();
        Log.d("MainActivity", "createNotification");
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra(EXTRA_KEY_UPDATE, 1);

        PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), pi);



    }
}
