package com.example.android.popularmovies.data.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by debeyo on 08/05/2016.
 */
@Database(
        version = FavoriteMoviesDatabase.VERSION,
        packageName = "com.example.android.popularmovies.data.provider"
)
public class FavoriteMoviesDatabase {
    private FavoriteMoviesDatabase() {
    }

    public static final int VERSION = 1;

    @Table(FavoriteMoviesColumns.class)
    public static final String FAVORITE_MOVIES = "favorite_movies";
}
