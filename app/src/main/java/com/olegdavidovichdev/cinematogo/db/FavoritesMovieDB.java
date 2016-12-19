package com.olegdavidovichdev.cinematogo.db;

import com.orm.SugarRecord;

/**
 * Created by Oleg on 29.11.2016.
 */

public class FavoritesMovieDB extends SugarRecord {

    private String name;
    private String release;
    private String poster;
    private boolean enabled;

    public FavoritesMovieDB() {
    }

    public FavoritesMovieDB(String name, String release, String poster, boolean enabled) {
        this.name = name;
        this.release = release;
        this.poster = poster;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "FavoritesMovieDB{" +
                "name='" + name + '\'' +
                ", release='" + release + '\'' +
                ", poster='" + poster + '\'' +
                ", enabled=" + enabled +
                '}';
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}