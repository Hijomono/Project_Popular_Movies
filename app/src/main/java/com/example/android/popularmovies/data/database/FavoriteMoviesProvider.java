package com.example.android.popularmovies.data.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by debeyo on 08/05/2016.
 */
@ContentProvider(
        authority = FavoriteMoviesProvider.AUTHORITY,
        database = FavoriteMoviesDatabase.class,
        packageName = "com.example.android.popularmovies.data.provider"
)
public class FavoriteMoviesProvider {
    public static final String AUTHORITY =
            "com.example.android.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String FAVORITE_MOVIES = "favorite_movies";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
    @TableEndpoint(table = FavoriteMoviesDatabase.FAVORITE_MOVIES) public static class FavoriteMovies{
        @ContentUri(
                path = Path.FAVORITE_MOVIES,
                type = "vnd.android.cursor.dir/favorite_movie",
                defaultSort = FavoriteMoviesColumns.TITLE + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITE_MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.FAVORITE_MOVIES + "/#",
                type = "vnd.android.cursor.item/favorite_movie",
                whereColumn = FavoriteMoviesColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(int id){
            return buildUri(Path.FAVORITE_MOVIES, String.valueOf(id));
        }
    }
}
