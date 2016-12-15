package com.olegdavidovichdev.cinematogo.db;

import com.orm.SugarRecord;

/**
 * Created by Oleg on 29.11.2016.
 */

public class FavoritesMovieDB extends SugarRecord {

    private String num;
    private String name;
    private String release;
    private String poster;

    public FavoritesMovieDB() {
    }

    public FavoritesMovieDB(String num, String name, String release, String poster) {
        this.num = num;
        this.name = name;
        this.release = release;
        this.poster = poster;
    }

    @Override
    public String toString() {
        return "FavoritesMovieDB{" +
                "num=" + num +
                ", name='" + name + '\'' +
                '}';
    }

    public String getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public String getRelease() {
        return release;
    }

    public String getPoster() {
        return poster;
    }


}
