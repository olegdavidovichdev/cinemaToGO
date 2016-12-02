package com.olegdavidovichdev.cinematogo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olegdavidovichdev.cinematogo.R;
import com.olegdavidovichdev.cinematogo.listener.RecyclerViewItemListener;
import com.olegdavidovichdev.cinematogo.model.Movie;


import java.util.List;

/**
 * Created by Oleg on 26.11.2016.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private int rowLayout;
    private Context context;
    private View.OnClickListener itemListener;
    private View.OnLongClickListener longClickListener;
    private RecyclerView r;

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        LinearLayout moviesLayout;
        TextView title;
        TextView release;
        TextView movieDescription;
        TextView rating;

        public MovieViewHolder(View itemView) {

            super(itemView);
            moviesLayout = (LinearLayout) itemView.findViewById(R.id.movies_layout);
            title = (TextView) itemView.findViewById(R.id.title);
            release = (TextView) itemView.findViewById(R.id.release);
            movieDescription = (TextView) itemView.findViewById(R.id.description);
            rating = (TextView) itemView.findViewById(R.id.rating);
        }
    }

    public MoviesAdapter(List<Movie> movies, Context context, RecyclerView r) {
        this.movies = movies;
        this.context = context;
        this.r = r;
        itemListener = new RecyclerViewItemListener(r, movies);
        longClickListener = new RecyclerViewItemListener(r, movies);
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_film, parent, false);
        view.setOnClickListener(itemListener);
        view.setOnLongClickListener(longClickListener);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        holder.title.setText(movies.get(position).getTitle());
        holder.release.setText(movies.get(position).getReleaseDate());
        holder.movieDescription.setText(movies.get(position).getOverview());
        holder.rating.setText(movies.get(position).getVoteAverage().toString());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


}
