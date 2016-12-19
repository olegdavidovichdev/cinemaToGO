package com.olegdavidovichdev.cinematogo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.adapter.FavoritesMovieAdapter;
import com.olegdavidovichdev.cinematogo.db.FavoritesMovieDB;
import com.olegdavidovichdev.cinematogo.service.NotificationReceiver;

import java.util.List;


public class FavoritesMovieActivity extends AppCompatActivity {

    private static final String TAG = FavoritesMovieActivity.class.getSimpleName();

    private List<FavoritesMovieDB> movieList;

    private SharedPreferences sp;
    private static final String APP_PREFERENCES = "app_preferences";

    private ListView listFavFilm;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_favorites);

        setToolbar();

        listFavFilm = (ListView) findViewById(R.id.list_fav_film);

        movieList = FavoritesMovieDB.listAll(FavoritesMovieDB.class);
        Log.d(TAG, "onCreate:movieList = " + movieList.toString());

        FavoritesMovieAdapter fma = new FavoritesMovieAdapter(this, movieList);
        listFavFilm.setAdapter(fma);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.fav_activity_name);
    //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            if (movieList.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesMovieActivity.this);
                builder.setMessage(R.string.dialog_delete_all);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sp = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor e = sp.edit();
                        for (int i = 0; i < movieList.size(); i++) {
                            e.remove(movieList.get(i).getName());
                        }
                        e.apply();

                        FavoritesMovieAdapter fma = (FavoritesMovieAdapter) listFavFilm.getAdapter();
                        for (int i = 0; i< movieList.size(); i++) {
                            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                                    i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            am.cancel(pendingIntent);
                        }

                        FavoritesMovieDB.deleteAll(FavoritesMovieDB.class);
                        movieList.clear();

                        fma.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.create().show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public ListView getListFavFilm() {
        return listFavFilm;
    }
}
