package com.olegdavidovichdev.cinematogo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.adapter.FavoritesMovieAdapter;
import com.olegdavidovichdev.cinematogo.db.FavoritesMovieDB;
import com.olegdavidovichdev.cinematogo.service.NotificationReceiver;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Oleg on 29.11.2016.
 */

public class FavoritesMovieActivity extends AppCompatActivity {

    private static final String TAG = FavoritesMovieActivity.class.getSimpleName();

    private Toolbar toolbar;
    private static FavoritesMovieAdapter adapter;
    private List<FavoritesMovieDB> movieList;

    private static int counter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_favorites);

        setToolbar();

        ListView listFavFilm = (ListView) findViewById(R.id.list_fav_film);

        movieList = FavoritesMovieDB.listAll(FavoritesMovieDB.class);
        Log.d(TAG, "onCreate:movieList = " + movieList.toString());

        adapter = new FavoritesMovieAdapter(this, movieList);

        listFavFilm.setAdapter(adapter);

        listFavFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.fav_film_title);
                String itemTitle = textView.getText().toString();

                List<FavoritesMovieDB> list = FavoritesMovieDB.find(FavoritesMovieDB.class, "name = ?", itemTitle);
                final FavoritesMovieDB m = list.get(0);


                String[] items = getResources().getStringArray(R.array.click_fav_movie);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(FavoritesMovieActivity.this, R.layout.click_fav_movie, items);

                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesMovieActivity.this);
                builder.setTitle("Notify, if the movie will be released on the screen?");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                counter++;
                                Log.d(TAG, "counter = " + counter);

                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.SECOND, 15);
                                /*String release = m.getRelease();

                                int targetYear = Integer.parseInt(release.substring(0, 4));
                                int targetMonth = Integer.parseInt(release.substring(5, 7));
                                int targetDay = Integer.parseInt(release.substring(8, 10));
                                int targetHour = new Random().nextInt(15 - 9) + 9;
                                int targetMinute = new Random().nextInt(60);

                                Calendar targetDate = new GregorianCalendar(targetYear, targetMonth - 1,
                                        targetDay, targetHour, targetMinute);


                                Log.d(TAG, targetDate.toString());*/

                                Intent intent = new Intent(getBaseContext(), NotificationReceiver.class);
                                intent.putExtra("film_name", m.getName());
                                intent.putExtra("film_release", m.getRelease());
                                intent.putExtra("film_poster", m.getPoster());
                                intent.putExtra("notification_id", counter);

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),
                                        counter, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                                am.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), pendingIntent);
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.fav_activity_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite_movie, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete_all) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesMovieActivity.this);
            builder.setMessage(R.string.dialog_delete_all);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FavoritesMovieDB.deleteAll(FavoritesMovieDB.class);
                    movieList.clear();
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "delete all = " + FavoritesMovieDB.listAll(FavoritesMovieDB.class));
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}
