package com.olegdavidovichdev.cinematogo.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.Tooltips;
import com.olegdavidovichdev.cinematogo.model.Movie;
import com.olegdavidovichdev.cinematogo.rest.ApiClient;
import com.olegdavidovichdev.cinematogo.rest.ApiInterface;
import com.squareup.picasso.Picasso;

import it.sephiroth.android.library.tooltip.Tooltip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FilmDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = FilmDetailActivity.class.getSimpleName();
    private static final String API_KEY = "e755e32ddf1ac688f8617a68c325d41d";

    private SharedPreferences sp;
    private static final String APP_PREFERENCES = "app_preferences";
    private static final String APP_PREFERENCES_BASE_URL_IMAGES = "baseUrlImages";
    private String baseUrlImages;

    private static String posterId;

    private static String currentPosterSize;
    private static String currentLanguage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_film_detail);


        final ImageView img = (ImageView) findViewById(R.id.poster);
        final TextView tagline = (TextView) findViewById(R.id.tagline);
        final TextView runtime = (TextView) findViewById(R.id.runtime);
        final TextView budget = (TextView) findViewById(R.id.budget);
        final TextView popularity = (TextView) findViewById(R.id.popularity);
        final TextView adult = (TextView) findViewById(R.id.adult);
        final TextView overview = (TextView) findViewById(R.id.overview);
        final ProgressBar posterProgressBar = (ProgressBar) findViewById(R.id.poster_progress_bar);
        posterProgressBar.setVisibility(View.VISIBLE);

        ImageView imgRuntime = (ImageView) findViewById(R.id.img_runtime);
        ImageView imgPopularity = (ImageView) findViewById(R.id.img_popularity);
        ImageView imgBudget = (ImageView) findViewById(R.id.img_budget);
        ImageView imgAdult = (ImageView) findViewById(R.id.img_adult);

        imgRuntime.setOnClickListener(this);
        imgPopularity.setOnClickListener(this);
        imgBudget.setOnClickListener(this);
        imgAdult.setOnClickListener(this);


        final ProgressDialog pd = new ProgressDialog(this);
        pd.setIndeterminate(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getString(R.string.downloading_data));
        pd.show();

        final int id = getIntent().getIntExtra("id", -1);

        sp = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        baseUrlImages = sp.getString(APP_PREFERENCES_BASE_URL_IMAGES, "");

        Log.d(TAG, baseUrlImages + " " + currentLanguage);


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Movie> call = apiInterface.getMovieDetails(id, API_KEY, currentLanguage);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Log.d(TAG, response.body().getPosterPath() + "");

                posterId = response.body().getPosterPath();

                Log.d(TAG, baseUrlImages + currentPosterSize + posterId);

                Picasso.with(FilmDetailActivity.this)
                        .load(baseUrlImages + currentPosterSize + posterId)
                        .into(img, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                posterProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                posterProgressBar.setVisibility(View.GONE);
                                img.setImageDrawable(getResources().getDrawable(R.drawable.placeholder));
                                Toast.makeText(getApplicationContext(), "Picture downloading error", Toast.LENGTH_SHORT).show();
                            }
                        });


                // check fields
                if (response.body().getTagLine().equals("")) {
                    tagline.setText(getResources().getString(R.string.unknown));
                } else tagline.setText(String.format("«%s»", response.body().getTagLine()));

                if (response.body().getRuntime().equals("0")) {
                    runtime.setText(getResources().getString(R.string.unknown));
                } else runtime.setText(String.format("%s %s", response.body().getRuntime(), getResources().getString(R.string.minutes)));

                if (response.body().getBudget().equals("0")) {
                    budget.setText(getResources().getString(R.string.unknown));
                } else budget.setText(String.format("%s %s", response.body().getBudget(), getResources().getString(R.string.dollar)));


                popularity.setText(String.format("%s", response.body().getPopularity()));
                adult.setText(String.format("%s ", response.body().getAdult()));
                overview.setText(response.body().getOverview());

                pd.hide();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error, try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setPosterSize(String posterSize) {
        FilmDetailActivity.currentPosterSize = posterSize;
    }

    public static void setLanguage(String language) {
        FilmDetailActivity.currentLanguage = language;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_runtime:
                new Tooltips(v, 1, Tooltip.Gravity.TOP, 2500, getString(R.string.tooltip_runtime));
                break;
            case R.id.img_popularity:
                new Tooltips(v, 2, Tooltip.Gravity.BOTTOM, 2500, getString(R.string.tooltip_popularity));
                break;
            case R.id.img_budget:
                new Tooltips(v, 3, Tooltip.Gravity.TOP, 2500, getString(R.string.tooltip_budget));
                break;
            case R.id.img_adult:
                new Tooltips(v, 4, Tooltip.Gravity.BOTTOM, 4500, getString(R.string.tooltip_adult));
                break;
        }
    }
}
