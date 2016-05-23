package com.example.android.popularmovies.ui.moviesgrid;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.database.MoviesColumns;

/**
 * A placeholder fragment containing the grid view where the movie posters will be displayed.
 */
public abstract class MoviesGridFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;
    private static final String[] MOVIES_COLUMNS = {
            MoviesColumns._ID,
            MoviesColumns.MOVIE_ID,
            MoviesColumns.TITLE,
            MoviesColumns.POSTER_PATH,
            MoviesColumns.OVERVIEW,
            MoviesColumns.RATING,
            MoviesColumns.RELEASE_DATE
    };

    private MoviesCursorAdapter moviesGridAdapter;
    private GridView gridView;
    private Parcelable state;

    public MoviesGridFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        moviesGridAdapter = new MoviesCursorAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(moviesGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    makeCallback(cursor);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onPause() {
        state = gridView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                getUri(),
                MOVIES_COLUMNS,
                null,
                null,
                getOrder());
    }

    protected abstract String getOrder();

    protected abstract Uri getUri();

    protected abstract Uri getUriWithId(Cursor cursor);

    protected abstract void syncIfDataMissing(Cursor data);

    protected abstract void makeCallback(Cursor cursor);

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        syncIfDataMissing(data);
        moviesGridAdapter.swapCursor(data);
        if (state != null) {
            gridView.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesGridAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }
}
