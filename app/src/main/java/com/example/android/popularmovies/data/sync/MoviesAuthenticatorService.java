package com.example.android.popularmovies.data.sync;

/**
 * Created by debeyo on 11/05/2016.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class MoviesAuthenticatorService extends Service {
    private MoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new MoviesAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}