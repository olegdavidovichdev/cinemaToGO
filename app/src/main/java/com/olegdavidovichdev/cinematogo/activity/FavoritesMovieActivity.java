package com.olegdavidovichdev.cinematogo.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.adapter.FavoritesMovieAdapter;
import com.olegdavidovichdev.cinematogo.db.FavoritesMovieDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 29.11.2016.
 */

public class FavoritesMovieActivity extends AppCompatActivity {

    private static final String TAG = FavoritesMovieActivity.class.getSimpleName();

    private Toolbar toolbar;
    private static FavoritesMovieAdapter adapter;
    private List<FavoritesMovieDB> movieList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_favorites);

        setToolbar();

        ListView listFavFilm = (ListView) findViewById(R.id.list_fav_film);

        movieList = FavoritesMovieDB.listAll(FavoritesMovieDB.class);

        adapter = new FavoritesMovieAdapter(this, movieList);

        listFavFilm.setAdapter(adapter);

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
