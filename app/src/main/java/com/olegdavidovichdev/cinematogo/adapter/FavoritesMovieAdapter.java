package com.olegdavidovichdev.cinematogo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.db.FavoritesMovieDB;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Oleg on 01.12.2016.
 */

public class FavoritesMovieAdapter extends BaseAdapter {

    private static final String TAG = FavoritesMovieAdapter.class.getSimpleName();

    private Context ctx;
    private LayoutInflater layoutInflater;
    private List<FavoritesMovieDB> list;


    public FavoritesMovieAdapter(Context ctx, List<FavoritesMovieDB> list) {
        this.ctx = ctx;
        this.layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_fav_film, parent, false);
        }

        final FavoritesMovieDB movie = list.get(position);

        ((TextView) view.findViewById(R.id.fav_film_title)).setText(movie.getName());
        ((TextView) view.findViewById(R.id.fav_film_release)).setText(movie.getRelease());

        //
        SharedPreferences myPrefs = ctx.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);


        String imageUrl = myPrefs.getString("baseUrlImages", null);
        String size = "w154";
        String posterPath = movie.getPoster();

        Log.d(TAG, imageUrl + size + posterPath);

        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.fav_film_progress_bar);
        pb.setVisibility(View.VISIBLE);
        Picasso.with(view.getContext())
                .load(imageUrl + size + posterPath)
                .into(((ImageView) view.findViewById(R.id.fav_film_image)), new Callback() {
                    @Override
                    public void onSuccess() {
                        pb.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        final ImageView bin = (ImageView) view.findViewById(R.id.fav_film_bin);
        bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure you want to delete '" + movie.getName() + "' from Favorites?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        movie.delete();
                        list.remove(position);
                        FavoritesMovieAdapter.this.notifyDataSetChanged();
                        Log.i(TAG, FavoritesMovieDB.listAll(FavoritesMovieDB.class) + "");
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.create().show();
            }
        });

        return view;
    }
}
