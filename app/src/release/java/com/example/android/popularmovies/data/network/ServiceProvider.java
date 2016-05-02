package com.example.android.popularmovies.data.network;

import com.example.android.popularmovies.data.network.TheMovieDBService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by debeyo on 30/04/2016.
 */
public class ServiceProvider {
    private final static String BASE_URL = "http://api-themoviedb.org";

    public static TheMovieDBService.TheMovieDBAPI fetchMoviesService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        return retrofit.create(TheMovieDBService.TheMovieDBAPI.class);
    }

}
