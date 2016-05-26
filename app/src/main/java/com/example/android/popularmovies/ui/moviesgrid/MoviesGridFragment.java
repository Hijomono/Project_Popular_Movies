package com.example.android.popularmovies.ui.moviesgrid;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.database.MoviesColumns;
import com.squareup.picasso.Picasso;

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
    private RecyclerView gridView;
    private Parcelable state;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        moviesGridAdapter = new MoviesCursorAdapter(getActivity(), null) {
            @Override
            public void onBindViewHolder(final ViewHolder viewHolder, final Cursor cursor) {
                super.onBindViewHolder(viewHolder, cursor);
                final MyListItem myListItem = MyListItem.fromCursor(cursor);
                viewHolder.moviePosterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        makeCallback(myListItem.getMovieId());
                    }
                });
                Picasso.with(getActivity())
                        .load(myListItem.getPosterPath())
                        .into(viewHolder.moviePosterView);

            }
        };
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (RecyclerView) rootView.findViewById(R.id.movies_grid);
        gridView.setLayoutManager(new GridLayoutManager(
                gridView.getContext(),
                getActivity().getResources().getInteger(R.integer.grid_columns)));
        gridView.setAdapter(moviesGridAdapter);
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
        state = gridView.getLayoutManager().onSaveInstanceState();
        super.onPause();
    }

    /**
     * Gives the order to query movies with the loader.
     *
     * @return the order in which the movies have to fill the grid.
     */
    protected abstract String getOrder();

    /**
     * Gives the URI of the database table to query movies with the loader.
     *
     * @return the URI of the table that matches the fragment class.
     */
    protected abstract Uri getUri();

    /**
     * Activates the SyncAdapter if the cursor from the database is empty,
     * except for favorite movies, that may be empty.
     *
     * @param data the cursor retrieved by the loader.
     */
    protected abstract void syncIfDataMissing(Cursor data);

    /**
     * Calls onItemClicked with an inexact URI that matches the fragment class.
     *
     * @param movieId the id of the movie clicked.
     */
    protected abstract void makeCallback(int movieId);

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                getUri(),
                MOVIES_COLUMNS,
                null,
                null,
                getOrder());
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        syncIfDataMissing(data);
            moviesGridAdapter.swapCursor(data);
        if (state != null) {
            gridView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesGridAdapter.swapCursor(null);
    }

    /**
     * A callback interface that allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * Launches the detail fragment, in a new activity or the same, to show a movie's details
         * when it is clicked.
         *
         * @param movieUri the URI of the clicked movie.
         */
        void onItemSelected(Uri movieUri);
    }
}
