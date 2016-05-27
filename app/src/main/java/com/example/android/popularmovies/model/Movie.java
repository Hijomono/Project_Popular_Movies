package com.example.android.popularmovies.model;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesDatabase;

/**
 * Created by debeyo on 03/03/2016.
 */
public class Movie {

    private static final String BASE_PICASSO_URL = "http://image.tmdb.org/t/p/w185";

    private final int id;
    private final String title;
    private final String poster_path;
    private final String overview;
    private final String vote_average;
    private final String release_date;

    private Movie(final Builder builder) {
        id = builder.id;
        title = builder.title;
        poster_path = builder.poster_path;
        overview = builder.overview;
        vote_average = builder.vote_average;
        release_date = builder.release_date;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    /**
     * {@code Movie} builder static inner class.
     */
    public static final class Builder {
        private int id;
        private String title;
        private String poster_path;
        private String overview;
        private String vote_average;
        private String release_date;

        private Builder() {
        }

        /**
         * Sets the {@code id} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code id} to set
         * @return a reference to this Builder
         */
        public Builder id(final int val) {
            id = val;
            return this;
        }

        /**
         * Sets the {@code title} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code title} to set
         * @return a reference to this Builder
         */
        public Builder title(final String val) {
            title = val;
            return this;
        }

        /**
         * Sets the {@code poster_path} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code poster_path} to set
         * @return a reference to this Builder
         */
        public Builder poster_path(final String val) {
            poster_path = val;
            return this;
        }

        /**
         * Sets the {@code overview} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code overview} to set
         * @return a reference to this Builder
         */
        public Builder overview(final String val) {
            overview = val;
            return this;
        }

        /**
         * Sets the {@code vote_average} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code vote_average} to set
         * @return a reference to this Builder
         */
        public Builder vote_average(final String val) {
            vote_average = val;
            return this;
        }

        /**
         * Sets the {@code release_date} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code release_date} to set
         * @return a reference to this Builder
         */
        public Builder release_date(final String val) {
            release_date = val;
            return this;
        }

        /**
         * Returns a {@code Movie} built from the parameters previously set.
         *
         * @return a {@code Movie} built with parameters of this {@code Movie.Builder}
         */
        public Movie build() {
            return new Movie(this);
        }
    }

    /**
     * Creates the Uri to be used by Picasso.
     *
     * @return a Uri built with the {@code poster_path} of this {@code Movie}
     */
    public String getPicassoUri() {
        return BASE_PICASSO_URL + poster_path;
    }

    /**
     * Adds "/10" to {@code vote_average}.
     *
     * @return the String to be shown on MovieDetailsFragment
     */
    public String getRatingOutOfTen() {
        return vote_average + "/10";
    }

    /**
     * Checks if the {@code Movie} in the favorite movies table.
     *
     * @param context the context needed to create a readable database.
     * @return true if the {@code Movie} exists in the database, false if it doesn't.
     */
    public boolean isFavorite(Context context) {
        final SQLiteDatabase db = com.example.android.popularmovies.data.provider.MoviesDatabase
                .getInstance(context).getReadableDatabase();
        return DatabaseUtils.longForQuery(
                db,
                "select count(*) from "
                        + MoviesDatabase.FAVORITE_MOVIES
                        + " where "
                        + MoviesColumns.MOVIE_ID
                        + "=? limit 1",
                new String[]{String.valueOf(id)}
        ) > 0;
    }
}
