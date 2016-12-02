package com.olegdavidovichdev.cinematogo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.adapter.MoviesAdapter;
import com.olegdavidovichdev.cinematogo.model.Movie;
import com.olegdavidovichdev.cinematogo.model.MovieResponse;
import com.olegdavidovichdev.cinematogo.rest.ApiClient;
import com.olegdavidovichdev.cinematogo.rest.ApiInterface;
import com.olegdavidovichdev.cinematogo.service.CheckConfigurationService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_KEY = "e755e32ddf1ac688f8617a68c325d41d";
    private static final String TASK_TAG = "periodicTask";

    private SharedPreferences sp;

    private ProgressBar pb;
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private static String language;
    private String sync;
    private Boolean notif;
    private String posterSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);



        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        pb = (ProgressBar) findViewById(R.id.progress_bar);
        pb.setIndeterminate(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerView.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);


        String tempSync = "";

       if (sp.getAll().size() == 1) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("language", "en");
            editor.putString("sync", "10800");
            editor.putBoolean("notif", true);
            editor.putString("posterSize", "w500");
            editor.commit();

            language = sp.getString("language", null);
            sync = sp.getString("sync", null);
            notif = sp.getBoolean("notif", false);
            posterSize = sp.getString("posterSize", null);

            tempSync = sync;

            Log.d(TAG, "if = " + language + " / " + sync + " / " + notif + " / " + posterSize);
        }
        else {
           language = sp.getString("language", null);
           sync = sp.getString("sync", null);
           notif = sp.getBoolean("notif", false);
           posterSize = sp.getString("posterSize", null);
           Log.d(TAG, "else = " + language + " / " + sync + " / " + notif + " / " + posterSize);

           if (!tempSync.equals(sync)) {
               CheckConfigurationService.periodicSync(this, Long.parseLong(sync), Long.parseLong(sync) - 10, TASK_TAG, true, true);
               if (!notif) CheckConfigurationService.setNotify(false);
           }

           tempSync = sync;
        }

        Log.d(TAG, "current = " + sp.getAll().toString());

        FilmDetailActivity.setPosterSize(posterSize);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MovieResponse> call = apiService.getUpcomingMovies(API_KEY, language);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                //  Log.d(TAG, "response" + response.body());
                pb.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                List<Movie> movies = response.body().getResults();
                recyclerView.setAdapter(new MoviesAdapter(movies, getApplicationContext(), recyclerView));
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d(TAG, "t" + t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent menuIntent;
        if (item.getItemId() == R.id.menu_settings) {
            menuIntent = new Intent(this, SettingsActivity.class);
            startActivity(menuIntent);
        }
        if (item.getItemId() == R.id.menu_favorites) {
            menuIntent = new Intent(this, FavoritesMovieActivity.class);
            startActivity(menuIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    //вспомогательный, потом можно удалить
    public void delete() {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("language");
        editor.remove("sync");
        editor.remove("notif");
        editor.remove("posterSize");
        editor.commit();
    }

    public static String getCurrentLanguage() {
        return language;
    }
}
