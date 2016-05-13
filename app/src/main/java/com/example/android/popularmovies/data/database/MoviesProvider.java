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
        authority = MoviesProvider.AUTHORITY,
        database = MoviesDatabase.class,
        packageName = "com.example.android.popularmovies.data.provider"
)
public class MoviesProvider {
    public static final String AUTHORITY =
            "com.example.android.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String FAVORITE_MOVIES = "favorite_movies";
        String POPULAR_MOVIES = "popular_movies";
        String TOP_RATED_MOVIES = "top_rated_movies";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }
    @TableEndpoint(table = MoviesDatabase.FAVORITE_MOVIES) public static class FavoriteMovies{
        @ContentUri(
                path = Path.FAVORITE_MOVIES,
                type = "vnd.android.cursor.dir/favorite_movie",
                defaultSort = MoviesColumns.TITLE + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITE_MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.FAVORITE_MOVIES + "/#",
                type = "vnd.android.cursor.item/favorite_movie",
                whereColumn = MoviesColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(int id){
            return buildUri(Path.FAVORITE_MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MoviesDatabase.POPULAR_MOVIES) public static class PopularMovies{
        @ContentUri(
                path = Path.POPULAR_MOVIES,
                type = "vnd.android.cursor.dir/popular_movie",
                defaultSort = MoviesColumns.TITLE + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.POPULAR_MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.POPULAR_MOVIES + "/#",
                type = "vnd.android.cursor.item/popular_movie",
                whereColumn = MoviesColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(int id){
            return buildUri(Path.POPULAR_MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MoviesDatabase.TOP_RATED_MOVIES) public static class TopRatedMovies{
        @ContentUri(
                path = Path.TOP_RATED_MOVIES,
                type = "vnd.android.cursor.dir/top_rated_movie",
                defaultSort = MoviesColumns.TITLE + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.TOP_RATED_MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.TOP_RATED_MOVIES + "/#",
                type = "vnd.android.cursor.item/top_rated_movie",
                whereColumn = MoviesColumns.MOVIE_ID,
                pathSegment = 1)
        public static Uri withId(int id){
            return buildUri(Path.TOP_RATED_MOVIES, String.valueOf(id));
        }
    }
}
