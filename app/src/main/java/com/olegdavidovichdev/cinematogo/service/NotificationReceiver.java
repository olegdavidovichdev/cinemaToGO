package com.olegdavidovichdev.cinematogo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.common.base.Defaults;
import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.activity.FavoritesMovieActivity;
import com.olegdavidovichdev.cinematogo.activity.MainActivity;
import com.olegdavidovichdev.cinematogo.network.CheckNetwork;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Oleg on 02.12.2016.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();
    private SharedPreferences sp;
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String APP_PREFERENCES_BASE_URL_IMAGES = "baseUrlImages";

    private static final String EXTRA_KEY = "key";

    private static String url;


    @Override
    public void onReceive(Context context, Intent intent) {

        int key = intent.getIntExtra(EXTRA_KEY, 0);

        switch (key) {

            case 1:
                Intent in = new Intent(context, MainActivity.class);
                PendingIntent p = PendingIntent.getActivity(context, 0, in, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.notification_update);
                builder.setContentTitle(context.getResources().getString(R.string.app_name));
                builder.setContentText("API configuration was successfully updated");
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher));
                builder.setDefaults(Notification.DEFAULT_ALL);
                builder.setContentIntent(p);
                builder.setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
                break;

            case 2:
                String title = intent.getStringExtra("film_name");
                String release = intent.getStringExtra("film_release");
                String poster = intent.getStringExtra("film_poster");
                int notificationId = intent.getIntExtra("notification_id", 0);

                sp = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

                String http = sp.getString(APP_PREFERENCES_BASE_URL_IMAGES, "");

                url = http + "w500" + poster;

                Log.d(TAG, title + " / " + release + " / " + poster);
                Log.d(TAG, url);
                Log.d(TAG, "id = " + notificationId);

                Intent intentInfo = new Intent(context, FavoritesMovieActivity.class);

                PendingIntent pi = PendingIntent.getActivity(context, 0, intentInfo, 0);
                builder = new NotificationCompat.Builder(context);
                builder.setTicker(context.getString(R.string.notif_ticker));
                builder.setSmallIcon(R.drawable.notification_favorites);
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.launcher));
                builder.setContentTitle(title);
                builder.setContentText(context.getString(R.string.notif_content_text));
                builder.setDefaults(Notification.DEFAULT_SOUND);
                builder.setContentIntent(pi);
                builder.setAutoCancel(true);
                builder.setGroupSummary(true);

                if (CheckNetwork.isInternetAvailable(context)) {
                    try {
                        builder.setStyle(new android.support.v4.app.NotificationCompat.BigPictureStyle()
                                .setSummaryText(context.getString(R.string.notif_content_text)).bigPicture(getBitmap()));
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.notify(notificationId, builder.build());
                break;
        }
    }

    public Bitmap getBitmap() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Bitmap> callable = new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws IOException {
                return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            }
        };

        Future<Bitmap> future = executor.submit(callable);
        executor.shutdown();

        return future.get();
    }
}





