package com.example.android.popularmovies.data.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by debeyo on 29/04/2016.
 */
public class TheMovieDBService {

    public interface TheMovieDBAPI {
        @GET("/3/movie/{sortBy}")
        Call<FetchedMoviesList> getMovieList(
                @Path("sortBy") String sortBy,
                @Query("api_key") String apiKey);

        @GET("/3/movie/{movieId}/videos")
        Call<FetchedTrailersList> getTrailerList(
                @Path("movieId") String movieId,
                @Query("api_key") String apiKey);

        @GET("/3/movie/{movieId}/reviews")
        Call<FetchedReviewsList> getReviewList(
                @Path("movieId") String movieId,
                @Query("api_key") String apiKey);
    }
}
