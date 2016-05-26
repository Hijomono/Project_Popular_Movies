package com.example.android.popularmovies.data.repository;

import com.example.android.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by debeyo on 19/05/2016.
 */
public interface MoviesRepository {

    /**
     * Deletes data in popular movies table, adds new one from movies in the list, downloads poster
     * images that are not stored yet and updates movie_path in database in order for Picasso to
     * load images from internal storage.
     *
     * @param moviesList the list to be stored in the database.
     */
    void savePopularMovies(List<Movie> moviesList);
    /**
     * Deletes data in top rated movies table, adds new one from movies in the list, downloads poster
     * images that are not stored yet and updates movie_path in database in order for Picasso to
     * load images from internal storage.
     *
     * @param moviesList the list to be stored in the database.
     */
    void saveTopRatedMovies(List<Movie> moviesList);
    /**
     * Adds data from selected {@code Movie} to favorite movies table.
     *
     * @param movie the {@code Movie} to be stored in the database.
     */
    void addToFavorites(Movie movie);
    /**
     * Deletes data from selected {@code Movie} from favorite movies table.
     *
     * @param movie the {@code Movie} to be deleted from the database.
     */
    void removeFromFavorites(Movie movie);
    /**
     * For every poster file stored, checks if it can be accessed using the URIs stored in the
     * poster_path column of any table of the database, and if it can't, deletes the file.
     */
    void deleteUnusedPosters();
}
