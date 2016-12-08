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
import android.widget.Toast;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.adapter.MoviesAdapter;
import com.olegdavidovichdev.cinematogo.model.ConfigurationResponse;
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
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String APP_PREFERENCES_BASE_URL_IMAGES = "baseUrlImages";
    private static final String APP_PREFERENCES_BASE_SIZE_POSTER = "baseSizePosters";

    private SharedPreferences spa;
    private static final String SETTINGS_PREFERENCES_LANGUAGE = "language";
    private static final String SETTINGS_PREFERENCES_SYNC = "sync";
    private static final String SETTINGS_PREFERENCES_SOUND_NOTIFICATION = "notif";
    private static final String SETTINGS_PREFERENCES_POSTER_SIZE = "posterSize";


    private static final long DEFAULT_PERIOD = 10800;
    private static final long DEFAULT_FLEX = 100;

    private ProgressBar pb;
    private RecyclerView recyclerView;

    private String language;
    private String sync;
    private Boolean notif;
    private String posterSize;

    private String baseUrlImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);

        pb = (ProgressBar) findViewById(R.id.progress_bar);
        pb.setIndeterminate(true);

        sp = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        if (!sp.contains(APP_PREFERENCES_BASE_URL_IMAGES)) {

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ConfigurationResponse> checkConfiguration = apiService.getConfiguration(API_KEY);
            checkConfiguration.enqueue(new Callback<ConfigurationResponse>() {
                @Override
                public void onResponse(Call<ConfigurationResponse> call, Response<ConfigurationResponse> response) {
                    baseUrlImages = response.body().getImages().getBaseUrl();

                    // add to preferences basic image url
                    SharedPreferences.Editor e = sp.edit();
                    e.putString(APP_PREFERENCES_BASE_URL_IMAGES, baseUrlImages);

                    e.apply();

                    Log.d(TAG, APP_PREFERENCES +" = " +  sp.getAll().toString());

                    Toast.makeText(getApplicationContext(), "App get new API configuration", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ConfigurationResponse> call, Throwable t) {
                    Log.d(TAG, "Fail to download Api configuration");
                    Toast.makeText(getApplicationContext(), "Fail to download Api configuration", Toast.LENGTH_LONG).show();
                }
            });

            CheckConfigurationService.periodicSync(this, DEFAULT_PERIOD, DEFAULT_FLEX, TASK_TAG, false, true);

        }
        spa = PreferenceManager.getDefaultSharedPreferences(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        recyclerView.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);

        if (!spa.contains(SETTINGS_PREFERENCES_LANGUAGE)) {
            language = "en";
        } else language = spa.getString(SETTINGS_PREFERENCES_LANGUAGE, null);
        language = spa.getString("language", null);
        sync = spa.getString("sync", null);
        notif = spa.getBoolean("notif", false);
        if (!spa.contains(SETTINGS_PREFERENCES_POSTER_SIZE)) {
            posterSize = "w500";
        } else posterSize = spa.getString(SETTINGS_PREFERENCES_POSTER_SIZE, null);


        FilmDetailActivity.setPosterSize(posterSize);
        FilmDetailActivity.setLanguage(language);

        Log.d(TAG, "current = " + spa.getAll().toString());

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
}
