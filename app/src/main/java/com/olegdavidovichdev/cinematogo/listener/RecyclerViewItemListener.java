package com.olegdavidovichdev.cinematogo.listener;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.activity.FilmDetailActivity;
import com.olegdavidovichdev.cinematogo.db.FavoritesMovieDB;
import com.olegdavidovichdev.cinematogo.model.Movie;

import java.util.List;


public class RecyclerViewItemListener implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView recyclerView;
    private List<Movie> movies;


    public RecyclerViewItemListener(RecyclerView recyclerView, List<Movie> movies) {
        this.recyclerView = recyclerView;
        this.movies = movies;
    }

    @Override
    public void onClick(View v) {
        Log.d("MainActivity", "onClick");
        int count = recyclerView.getChildLayoutPosition(v);

        Movie m = movies.get(count);

        int id = m.getId();

        Intent intent = new Intent(v.getContext(), FilmDetailActivity.class);
        intent.putExtra("id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        v.getContext().startActivity(intent);
    }


    @Override
    public boolean onLongClick(final View v) {

        String[] items = v.getResources().getStringArray(R.array.long_click_dialog_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(), R.layout.long_click_dialog_item, items);

        Log.d("MainActivity", "onLongClick");
        int count = recyclerView.getChildLayoutPosition(v);

        final Movie m = movies.get(count);

        android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(v.getContext());
        ad.setTitle(m.getTitle());
        ad.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:

                        FavoritesMovieDB f = new FavoritesMovieDB(m.getTitle(), m.getReleaseDate(), m.getPosterPath(), false);

                        List<FavoritesMovieDB> fullList = FavoritesMovieDB.listAll(FavoritesMovieDB.class);
                        Boolean isSameItem = false;

                        // check same movies
                        for (FavoritesMovieDB item : fullList) {
                            if (item.getName().equals(f.getName())) {
                                Toast.makeText(v.getContext(), R.string.same_film, Toast.LENGTH_SHORT).show();
                                isSameItem = true;
                                break;
                            }
                        }

                        if (!isSameItem) {
                            f.save();

                            Snackbar
                                    .make(v, v.getResources().getString(R.string.added_to_favorites), Snackbar.LENGTH_SHORT)
                                    .setAction("ОК", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {}
                                    })
                                    .show();
                        }
                }
            }
        });
        ad.create().show();
        return true;
    }
}
