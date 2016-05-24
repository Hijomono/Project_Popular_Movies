package com.example.android.popularmovies.data.repository;

import com.example.android.popularmovies.model.Movie;

import java.util.List;

/**
 * Created by debeyo on 19/05/2016.
 */
public interface MoviesRepository {

    void savePopularMovies(List<Movie> moviesList);
    void saveTopRatedMovies(List<Movie> moviesList);
    void addToFavorites(Movie movie);
    void removeFromFavorites(Movie movie);
    void deleteUnusedPosters();
}
