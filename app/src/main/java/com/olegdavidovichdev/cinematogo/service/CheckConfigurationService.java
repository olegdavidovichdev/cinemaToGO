package com.olegdavidovichdev.cinematogo.service;

import android.content.Context;
import android.content.SharedPreferences;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Oleg on 27.11.2016.
 */

public class CheckConfigurationService extends GcmTaskService
{
  //  private static final String TASK_TAG = "periodicTask";
    private static boolean notify;

    @Override
    public int onRunTask(TaskParams taskParams)
    {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ConfigurationResponse> checkConfiguration = apiService.getConfiguration("e755e32ddf1ac688f8617a68c325d41d");
        checkConfiguration.enqueue(new Callback<ConfigurationResponse>() {
            @Override
            public void onResponse(Call<ConfigurationResponse> call, Response<ConfigurationResponse> response) {

                SharedPreferences myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor e = myPrefs.edit();
                e.putString("baseUrlImages", response.body().getImages().getBaseUrl());
                e.apply();
                Log.d("MainActivity", "Url is updated = " + myPrefs.getAll().toString());
                if (notify) createNotification();
            }

            @Override
            public void onFailure(Call<ConfigurationResponse> call, Throwable t) {

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

        Toast.makeText(getApplicationContext(), "API configuration was successfully updated", Toast.LENGTH_LONG).show();

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setSmallIcon(android.R.drawable.stat_notify_sync);
        builder.setContentTitle("CinemaToGO");
        builder.setContentText("API configuration was successfully updated");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());*/
    }
}
