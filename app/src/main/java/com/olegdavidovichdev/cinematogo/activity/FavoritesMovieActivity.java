package com.olegdavidovichdev.cinematogo.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

    private List<FavoritesMovieDB> movieList;

    private static final String EXTRA_KEY = "key";

    private SharedPreferences sp;
    private static final String APP_PREFERENCES = "app_preferences";

    private static int counter = 0;

    private Bundle listViewState;

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

        listFavFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
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

                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sp = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        Log.d(TAG, sp.getAll().toString() + "");
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
                Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesMovieActivity.this);
                builder.setMessage(R.string.dialog_delete_all);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sp = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor e = sp.edit();
                        for (int i = 0; i < movieList.size(); i++) {
                            e.remove(String.valueOf(i));
                        }
                        e.apply();
                        Log.d(TAG, sp.getAll().toString());

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
                        Log.d(TAG, "delete all = " + FavoritesMovieDB.listAll(FavoritesMovieDB.class));


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
}
