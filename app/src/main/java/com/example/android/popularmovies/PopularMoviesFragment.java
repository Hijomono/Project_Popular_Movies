package com.example.android.popularmovies;

import android.database.Cursor;
import android.net.Uri;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesProvider;

/**
 * Created by debeyo on 14/05/2016.
 */
public class PopularMoviesFragment extends MoviesGridFragment {

    @Override
    protected String getOrder() {
        return MoviesColumns._ID + " ASC";
    }

    @Override
    protected Uri getUri() {
        return MoviesProvider.PopularMovies.CONTENT_URI;
    }

    @Override
    protected Uri getUriWithId(final Cursor cursor) {
        return MoviesProvider.PopularMovies.withId(cursor.getInt(cursor.getColumnIndex(MoviesColumns._ID)));
    }
}