package com.example.android.popularmovies.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesDatabase;
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
    public void addToFavorites(final Movie movie) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesColumns.MOVIE_ID, movie.getId());
        movieValues.put(MoviesColumns.TITLE, movie.getTitle());
        movieValues.put(MoviesColumns.POSTER_PATH, movie.getPosterPath());
        movieValues.put(MoviesColumns.OVERVIEW, movie.getOverview());
        movieValues.put(MoviesColumns.RATING, movie.getVoteAverage());
        movieValues.put(MoviesColumns.RELEASE_DATE, movie.getReleaseDate());
        context.getContentResolver().insert(
                MoviesProvider.FavoriteMovies.CONTENT_URI,
                movieValues);
    }

    @Override
    public void removeFromFavorites(final Movie movie) {
        context.getContentResolver().delete(
                MoviesProvider.FavoriteMovies.CONTENT_URI,
                MoviesColumns.MOVIE_ID + "=?",
                new String[]{String.valueOf(movie.getId())}
        );
    }

    @Override
    public void deleteUnusedPosters() {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String[] posterNames = directory.list();
        for (String posterName : posterNames) {
            deleteIfUnused(directory, posterName);
        }
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
            movieValues.put(MoviesColumns.RATING, movie.getVoteAverage());
            movieValues.put(MoviesColumns.RELEASE_DATE, movie.getReleaseDate());

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
        savePosters(tableUri, list);
    }

    /**
     * Take a list of {@code Movie} and calls savePoster on each one of them.
     *
     * @param tableUri  The Uri where the poster files' paths must be inserted.
     * @param movieList The list of {@code Movie} whose posters must be stored.
     */
    private void savePosters(final Uri tableUri, final List<Movie> movieList) {
        for (int i = 0; i < movieList.size(); i++) {
            Movie movie = movieList.get(i);
            savePoster(tableUri, movie);
        }
    }

    /**
     * Loads a {@code Movie} poster image into a file if it doesn't exist already
     * and updates poster_path column to that file URI.
     *
     * @param tableUri  The Uri where the poster file path must be inserted.
     * @param movie The {@code Movie} whose poster must be stored.
     */
    private void savePoster(final Uri tableUri, final Movie movie) {
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                ContextWrapper cw = new ContextWrapper(context);
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = new File(directory, movie.getId() + ".png");
                try {
                    if (!file.exists()) {
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.close();
                    }
                    Uri imageUri = Uri.fromFile(file);
                    updateMoviePoster(tableUri, movie, imageUri);
                    moviePosterTargets.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        moviePosterTargets.add(target);
        Picasso.with(context).load(movie.getPicassoUri()).into(target);
    }

    /**
     * Updates poster_path column to the URI of the stored image file.
     *
     * @param tableUri  The Uri where the poster file path must be inserted.
     * @param movie     The {@code Movie} whose poster must be stored.
     * @param posterUri The Uri of the image file to be inserted.
     */
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
    }

    /**
     * Deletes a poster file if its URI does not exist in the database.
     *
     * @param directory  The directory where the poster file is stored.
     * @param posterName The name of the image file to be checked.
     */
    private void deleteIfUnused(final File directory, final String posterName) {
        final String dirPath = "file:///data/data/com.example.android.popularmovies/app_imageDir/";
        String fullPosterPath = dirPath + posterName;
        if (!posterExistsInDatabase(fullPosterPath)) {
            File file = new File(directory, posterName);
            file.delete();
        }
    }

    /**
     * Checks if the URI of a poster image exists in the database.
     *
     * @param posterPath The Uri of the image file to be checked.
     * @return true if the poster URI exists in the database, false if it doesn't.
     */
    private boolean posterExistsInDatabase(final String posterPath) {
        return movieIsPopular(posterPath) || movieIsTopRated(posterPath) || movieIsFavorite(posterPath);
    }

    /**
     * Checks if the URI of a poster image exists in the popular movies table.
     *
     * @param posterPath The Uri of the image file to be checked.
     * @return true if the poster URI exists in the database, false if it doesn't.
     */
    private boolean movieIsPopular(final String posterPath) {
        final SQLiteDatabase db = com.example.android.popularmovies.data.provider.MoviesDatabase
                .getInstance(context).getReadableDatabase();
        return DatabaseUtils.longForQuery(
                db,
                "select count(*) from "
                        + MoviesDatabase.POPULAR_MOVIES
                        + " where "
                        + MoviesColumns.POSTER_PATH
                        + "=? limit 1",
                new String[]{posterPath}
        ) > 0;
    }

    /**
     * Checks if the URI of a poster image exists in the top rated movies table.
     *
     * @param posterPath The Uri of the image file to be checked.
     * @return true if the poster URI exists in the database, false if it doesn't.
     */
    private boolean movieIsTopRated(final String posterPath) {
        final SQLiteDatabase db = com.example.android.popularmovies.data.provider.MoviesDatabase
                .getInstance(context).getReadableDatabase();
        return DatabaseUtils.longForQuery(
                db,
                "select count(*) from "
                        + MoviesDatabase.TOP_RATED_MOVIES
                        + " where "
                        + MoviesColumns.POSTER_PATH
                        + "=? limit 1",
                new String[]{posterPath}
        ) > 0;
    }

    /**
     * Checks if the URI of a poster image exists in the favorite movies table.
     *
     * @param posterPath The Uri of the image file to be checked.
     * @return true if the poster URI exists in the database, false if it doesn't.
     */
    private boolean movieIsFavorite(final String posterPath) {
        final SQLiteDatabase db = com.example.android.popularmovies.data.provider.MoviesDatabase
                .getInstance(context).getReadableDatabase();
        return DatabaseUtils.longForQuery(
                db,
                "select count(*) from "
                        + MoviesDatabase.FAVORITE_MOVIES
                        + " where "
                        + MoviesColumns.POSTER_PATH
                        + "=? limit 1",
                new String[]{posterPath}
        ) > 0;
    }
}
