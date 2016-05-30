package com.example.android.popularmovies.model;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesDatabase;
import com.google.gson.annotations.SerializedName;

/**
 * Created by debeyo on 03/03/2016.
 */
public class Movie {

    private static final String BASE_PICASSO_URL = "http://image.tmdb.org/t/p/w185";

    private final int id;
    private final String title;
    @SerializedName("poster_path")
    private final String posterPath;
    private final String overview;
    @SerializedName("vote_average")
    private final String voteAverage;
    @SerializedName("release_date")
    private final String releaseDate;

    private Movie(final Builder builder) {
        id = builder.id;
        title = builder.title;
        posterPath = builder.posterPath;
        overview = builder.overview;
        voteAverage = builder.voteAverage;
        releaseDate = builder.releaseDate;
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

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * {@code Movie} builder static inner class.
     */
    public static final class Builder {
        private int id;
        private String title;
        private String posterPath;
        private String overview;
        private String voteAverage;
        private String releaseDate;

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
         * Sets the {@code posterPath} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code posterPath} to set
         * @return a reference to this Builder
         */
        public Builder posterPath(final String val) {
            posterPath = val;
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
         * Sets the {@code voteAverage} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code voteAverage} to set
         * @return a reference to this Builder
         */
        public Builder voteAverage(final String val) {
            voteAverage = val;
            return this;
        }

        /**
         * Sets the {@code releaseDate} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code releaseDate} to set
         * @return a reference to this Builder
         */
        public Builder releaseDate(final String val) {
            releaseDate = val;
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
     * @return a Uri built with the {@code posterPath} of this {@code Movie}
     */
    public String getPicassoUri() {
        return BASE_PICASSO_URL + posterPath;
    }

    /**
     * Adds "/10" to {@code voteAverage}.
     *
     * @return the String to be shown on MovieDetailsFragment
     */
    public String getRatingOutOfTen() {
        return voteAverage + "/10";
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
