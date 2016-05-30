package com.example.android.popularmovies.ui.moviesgrid;

/**
 * Created by debeyo on 30/05/2016.
 */
public interface MovieSelectedListener {
    /**
     * Calls onItemClicked with an inexact URI that matches the fragment class.
     *
     * @param movieId the id of the movie clicked.
     */
    void makeCallback(int movieId);
}
