package com.example.android.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by debeyo on 28/04/2016.
 */
public class PopularMoviesApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
}
