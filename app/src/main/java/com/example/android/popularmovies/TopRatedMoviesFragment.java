package com.example.android.popularmovies;

import android.database.Cursor;
import android.net.Uri;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesProvider;

/**
 * Created by debeyo on 14/05/2016.
 */
public class TopRatedMoviesFragment extends MoviesGridFragment {

    @Override
    protected String getOrder() {
        return MoviesColumns.RATING + " DESC";
    }

    @Override
    protected Uri getUri() {
        return MoviesProvider.TopRatedMovies.CONTENT_URI;
    }

    @Override
    protected Uri getUriWithId(final Cursor cursor) {
        return MoviesProvider.TopRatedMovies.withId(cursor.getInt(cursor.getColumnIndex(MoviesColumns._ID)));
    }
}