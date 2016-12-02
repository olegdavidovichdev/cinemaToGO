package com.olegdavidovichdev.cinematogo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.activity.FavoritesMovieActivity;

/**
 * Created by Oleg on 02.12.2016.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("film_name");
        String release = intent.getStringExtra("film_release");
        String poster = intent.getStringExtra("film_poster");

        SharedPreferences s = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        String http = s.getString("baseUrlImages", "");

        String url = http + "w45" + poster;

        Log.d(TAG, title + " / " + release + " / " + poster);
        Log.d(TAG, http + "w45" + poster);

        Intent intentInfo = new Intent(context, FavoritesMovieActivity.class);

        PendingIntent pi = PendingIntent.getActivity(context, 2, intentInfo, 0);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker("Your favorite film is out now!");
        builder.setSmallIcon(R.drawable.notif_small_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher));
        builder.setContentTitle(title);
        builder.setContentText("Film out on the screen today!");
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentIntent(pi);
        builder.setAutoCancel(true);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, builder.build());



    }
}
