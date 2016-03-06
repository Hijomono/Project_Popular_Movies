package com.example.android.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing the grid view where the movie posters will be displayed.
 */
public class MoviesGridFragment extends Fragment {

    private MoviesAdapter moviesGridAdapter;

    public MoviesGridFragment() {
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String moviesSortBy = prefs.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
        moviesTask.execute(moviesSortBy);
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

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the first page movies info in JSON Format and
         * pull out the data we need to construct the Movies.
         */
        private Movie[] getMoviesDataFromJson(String moviesJsonString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_RESULTS = "results";
            final String TMDB_ID = "id";
            final String TMDB_TITLE = "original_title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonString);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            Movie[] resultMovies = new Movie[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {

                int id;
                String title;
                String posterPath;
                String overview;
                String rating;
                String releaseDate;
                String posterUrl;

                // Get the JSON object representing the movie.
                JSONObject movieObject = moviesArray.getJSONObject(i);

                // Get the JSON objects needed to create the Movie.
                id = movieObject.getInt(TMDB_ID);
                title = movieObject.getString(TMDB_TITLE);
                posterPath = movieObject.getString(TMDB_POSTER_PATH);
                overview = movieObject.getString(TMDB_OVERVIEW);
                rating = movieObject.getString(TMDB_VOTE_AVERAGE);
                releaseDate = movieObject.getString(TMDB_RELEASE_DATE);

                // Create the string that picasso will use as URL
                posterUrl = getString(R.string.base_picasso_url) + getString(R.string.picasso_w185_size) + posterPath;

                resultMovies[i] = new Movie(id, title, posterUrl, overview, rating, releaseDate);
            }

            return resultMovies;

        }

        @Override
        protected Movie[] doInBackground(final String... params) {

            if (params.length == 0) {
                return null;
            }


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Construct the URL for the TheMovieDB query.
                String theMovieDBUrl = "http://api.themoviedb.org/3/movie/" + params[0] + "?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY;

                Uri builtUri = Uri.parse(theMovieDBUrl);
                URL url = new URL(builtUri.toString());


                // Create the request to TheMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("MoviesGridFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MoviesGridFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                moviesGridAdapter.clear();
                moviesGridAdapter.addAll(result);
            }
        }
    }
}
