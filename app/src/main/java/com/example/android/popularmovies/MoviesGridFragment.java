package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.data.database.FavoriteMoviesColumns;
import com.example.android.popularmovies.data.database.FavoriteMoviesProvider;
import com.example.android.popularmovies.data.network.FetchedMoviesList;
import com.example.android.popularmovies.data.network.ServiceProvider;
import com.example.android.popularmovies.data.network.TheMovieDBService;
import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing the grid view where the movie posters will be displayed.
 */
public class MoviesGridFragment extends Fragment {

    private MoviesAdapter moviesGridAdapter;
    private Call<FetchedMoviesList> call;
    private FetchedMoviesList fetchedMoviesList;
    private List<Movie> moviesList;

    public MoviesGridFragment() {
    }

    /**
     * Calls themoviedb.org for movies, fills moviesList with them
     * and populates the adapter with moviesList.
     */
    private void fetchMoviesFromTheMovieDb(String moviesSortBy) {
        TheMovieDBService.TheMovieDBAPI service = ServiceProvider.fetchMoviesService();
        call = service.getMovieList(moviesSortBy, BuildConfig.THE_MOVIE_DB_API_KEY);
        call.enqueue(new Callback<FetchedMoviesList>() {
            @Override
            public void onResponse(final Call<FetchedMoviesList> call, final Response<FetchedMoviesList> response) {
                try {
                    fetchedMoviesList = response.body();
                    moviesList = fetchedMoviesList.getResults();
                    moviesGridAdapter.clear();
                    moviesGridAdapter.addAll(moviesList);
                } catch (NullPointerException e) {
                    Toast toast = null;
                    if (response.code() == 401) {
                        toast = Toast.makeText(getActivity(), "Unauthenticated", Toast.LENGTH_SHORT);
                    } else if (response.code() >= 400) {
                        toast = Toast.makeText(getActivity(), "Client Error " + response.code()
                                + " " + response.message(), Toast.LENGTH_SHORT);
                    }
                    toast.show();
                }
            }

            @Override
            public void onFailure(final Call<FetchedMoviesList> call, final Throwable t) {
                Log.e("getMovieList threw: ", t.getMessage());
            }
        });
    }

    /**
     * Generates Movies from the database, fills moviesList with them
     * and populates the adapter with moviesList.
     */
    private void fetchMoviesFromFavorites() {
        moviesList.clear();
        Cursor favMoviesCursor = getActivity().getContentResolver().query(
                FavoriteMoviesProvider.FavoriteMovies.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (favMoviesCursor != null) {
            try {
                while (favMoviesCursor.moveToNext()) {
                    Movie movie = Movie.newBuilder()
                            .id(favMoviesCursor.getInt(favMoviesCursor.getColumnIndex(FavoriteMoviesColumns.MOVIE_ID)))
                            .title(favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoriteMoviesColumns.TITLE)))
                            .poster_path(favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoriteMoviesColumns.POSTER_PATH)))
                            .overview(favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoriteMoviesColumns.OVERVIEW)))
                            .vote_average(favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoriteMoviesColumns.RATING)))
                            .release_date(favMoviesCursor.getString(favMoviesCursor.getColumnIndex(FavoriteMoviesColumns.RELEASE_DATE)))
                            .build();
                    moviesList.add(movie);
                }
            } finally {
                favMoviesCursor.close();
            }
            moviesGridAdapter.clear();
            moviesGridAdapter.addAll(moviesList);
        }
    }

    /**
     * Fills moviesList and the adapter depending on the sort criteria.
     */
    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String moviesSortBy = prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
            if (moviesSortBy.equals(getString(R.string.pref_sort_by_favorite))) {
                fetchMoviesFromFavorites();
            } else {
                fetchMoviesFromTheMovieDb(moviesSortBy);
            }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        moviesGridAdapter = new MoviesAdapter(
                getActivity(),
                new ArrayList<Movie>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(moviesGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Movie movieClicked = moviesGridAdapter.getItem(position);
                startActivity(MovieDetailsActivity.launchDetailsIntent(movieClicked, getActivity()));
            }
        });

        return rootView;
    }
}
