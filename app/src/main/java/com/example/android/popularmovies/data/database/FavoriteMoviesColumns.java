package com.example.android.popularmovies.data.database;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by debeyo on 08/05/2016.
 */
public class FavoriteMoviesColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    public static final String MOVIE_ID = "movie_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String POSTER_PATH = "poster_path";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String OVERVIEW = "overview";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RATING = "rating";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RELEASE_DATE = "release_date";
}
