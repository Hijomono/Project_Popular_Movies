package com.example.android.popularmovies.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesProvider;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by debeyo on 19/05/2016.
 */
public class MoviesStorage implements MoviesRepository {
    public static final String LOG_TAG = MoviesStorage.class.getSimpleName();

    private final Context context;
    private final List<Target> moviePosterTargets = new ArrayList<>();

    public MoviesStorage(final Context context) {
        this.context = context;
    }

    @Override
    public void savePopularMovies(final List<Movie> moviesList) {
        addMovieListToDatabase(moviesList, MoviesProvider.PopularMovies.CONTENT_URI);
    }

    @Override
    public void saveTopRatedMovies(final List<Movie> moviesList) {
        addMovieListToDatabase(moviesList, MoviesProvider.TopRatedMovies.CONTENT_URI);
    }

    @Override
    public void saveFavoriteMovie(final Movie movie) {

    }

    /**
     * Take a list of movies and inserts it into a table of the database
     * after deleting its previous content.
     *
     * @param list     The list to be stored as popular movies.
     * @param tableUri The Uri where the data must be inserted.
     */
    private void addMovieListToDatabase(List<Movie> list, Uri tableUri) {
        Vector<ContentValues> moviesVector = new Vector<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Movie movie = list.get(i);
            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesColumns.MOVIE_ID, movie.getId());
            movieValues.put(MoviesColumns.TITLE, movie.getTitle());
            movieValues.put(MoviesColumns.POSTER_PATH, movie.getPicassoUri());
            movieValues.put(MoviesColumns.OVERVIEW, movie.getOverview());
            movieValues.put(MoviesColumns.RATING, movie.getVote_average());
            movieValues.put(MoviesColumns.RELEASE_DATE, movie.getRelease_date());

            moviesVector.add(movieValues);
        }
        ContentValues[] cvArray = new ContentValues[moviesVector.size()];
        moviesVector.toArray(cvArray);
        context.getContentResolver().delete(
                tableUri,
                null,
                null);
        context.getContentResolver().bulkInsert(
                tableUri,
                cvArray);
        Log.e(LOG_TAG, "Movies added to " + tableUri);
        savePosters(tableUri, list);
        Log.e(LOG_TAG, "Posters loaded for " + tableUri);
    }

    private void savePosters(final Uri tableUri, final List<Movie> movieList) {
        for (int i = 0; i < movieList.size(); i++) {
            Movie movie = movieList.get(i);
            savePoster(tableUri, movie);
            Log.e(LOG_TAG, "2." + i);
        }
        Log.e(LOG_TAG, "List posters saved");
    }

    private void savePoster(final Uri tableUri, final Movie movie) {
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.e(LOG_TAG, "Bitmap loaded for " + movie.getTitle());
                ContextWrapper cw = new ContextWrapper(context);
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = new File(directory, movie.getId() + ".png");
                try {
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                    Uri imageUri = Uri.fromFile(file);
                    updateMoviePoster(tableUri, movie, imageUri);
                    moviePosterTargets.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(LOG_TAG, "IOException for " + movie.getTitle());
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(LOG_TAG, "Bitmap failed for " + movie.getTitle());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e(LOG_TAG, "Prepared for " + movie.getTitle());
            }
        };

        moviePosterTargets.add(target);
        Picasso.with(context).load(movie.getPicassoUri()).into(target);
        Log.e(LOG_TAG, "1." + movie.getTitle());
    }

    private void updateMoviePoster(final Uri tableUri, final Movie movie, final Uri posterUri) {
        ContentValues moviePosterValue = new ContentValues();
        moviePosterValue.put(MoviesColumns.POSTER_PATH, posterUri.toString());
        String selection = MoviesColumns.MOVIE_ID + " = ?";
        String[] selectionArgs = {"" + movie.getId()};
        context.getContentResolver().update(
                tableUri,
                moviePosterValue,
                selection,
                selectionArgs);
        Log.e(LOG_TAG, movie.getTitle() + " Uri updated");
    }
}
