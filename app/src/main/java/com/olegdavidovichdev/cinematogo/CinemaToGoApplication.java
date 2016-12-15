package com.olegdavidovichdev.cinematogo;

import android.app.Application;

import com.orm.SugarContext;

/**
 * Created by Oleg on 28.11.2016.
 */

public class CinemaToGoApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}

