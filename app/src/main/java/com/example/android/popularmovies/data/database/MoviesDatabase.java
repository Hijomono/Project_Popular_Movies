package com.example.android.popularmovies.data.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by debeyo on 08/05/2016.
 */
@Database(
        version = MoviesDatabase.VERSION,
        packageName = "com.example.android.popularmovies.data.provider"
)
public class MoviesDatabase {
    private MoviesDatabase() {
    }

    public static final int VERSION = 1;

    @Table(MoviesColumns.class)
    public static final String FAVORITE_MOVIES = "favorite_movies";

    @Table(MoviesColumns.class)
    public static final String POPULAR_MOVIES = "popular_movies";

    @Table(MoviesColumns.class)
    public static final String TOP_RATED_MOVIES = "top_rated_movies";
}
