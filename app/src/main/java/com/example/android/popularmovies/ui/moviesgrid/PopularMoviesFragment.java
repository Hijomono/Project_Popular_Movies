package com.example.android.popularmovies.ui.moviesgrid;

import android.database.Cursor;
import android.net.Uri;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesProvider;
import com.example.android.popularmovies.data.sync.MoviesSyncAdapter;

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
    protected void syncIfDataMissing(final Cursor data) {
        if (!data.moveToFirst()) {
            MoviesSyncAdapter.syncImmediately(getActivity());
        }
    }

    @Override
    protected void makeCallback(int movieCursorId) {
        ((Callback) getActivity()).onItemSelected(MoviesProvider.PopularMovies.withId(movieCursorId));
    }
}