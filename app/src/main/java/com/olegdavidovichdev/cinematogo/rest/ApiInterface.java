package com.olegdavidovichdev.cinematogo.rest;

import com.olegdavidovichdev.cinematogo.model.ConfigurationResponse;
import com.olegdavidovichdev.cinematogo.model.Movie;
import com.olegdavidovichdev.cinematogo.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(@Query("api_key") String apiKey, @Query("language") String language);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") int id, @Query("api_key") String apiKey, @Query("language") String language);

    @GET("configuration")
    Call<ConfigurationResponse> getConfiguration(@Query("api_key") String apiKey);


}
