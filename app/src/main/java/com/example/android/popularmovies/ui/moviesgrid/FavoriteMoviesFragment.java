package com.example.android.popularmovies.ui.moviesgrid;

import android.database.Cursor;
import android.net.Uri;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesProvider;

/**
 * Created by debeyo on 14/05/2016.
 */
public class FavoriteMoviesFragment extends MoviesGridFragment {

    @Override
    protected String getOrder() {
        return MoviesColumns.TITLE + " ASC";
    }

    @Override
    protected Uri getUri() {
        return MoviesProvider.FavoriteMovies.CONTENT_URI;
    }

    @Override
    protected void syncIfDataMissing(final Cursor data) {}

    @Override
    public void makeCallback(int movieId) {
        ((Callback) getActivity()).onItemSelected(MoviesProvider.FavoriteMovies.withId(movieId));
    }
}
