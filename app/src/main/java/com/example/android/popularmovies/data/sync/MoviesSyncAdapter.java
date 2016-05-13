package com.example.android.popularmovies.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesProvider;
import com.example.android.popularmovies.data.network.FetchedMoviesList;
import com.example.android.popularmovies.data.network.ServiceProvider;
import com.example.android.popularmovies.data.network.TheMovieDBService;
import com.example.android.popularmovies.model.Movie;

import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by debeyo on 11/05/2016.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public static final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();

    private static final String SORT_BY_POPULAR_PATH = "popular";
    private static final String SORT_BY_TOP_RATED_PATH = "top_rated";

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        fetchPopularMovies();
        fetchTopRatedMovies();
    }

    /**
     * Calls themoviedb.org for movies, fills moviesList with them
     * and populates the adapter with moviesList.
     *
     * @param moviesSortBy The path used to access themoviedb.org,
     *                     it can be either popular or top_rated.
     */
    private void fetchMoviesFromTheMovieDb(final String moviesSortBy, final Callback<FetchedMoviesList> callback) {
        TheMovieDBService.TheMovieDBAPI service = ServiceProvider.fetchMoviesService();
        Call<FetchedMoviesList> call = service.getMovieList(moviesSortBy, BuildConfig.THE_MOVIE_DB_API_KEY);
        call.enqueue(callback);
    }

    private void fetchPopularMovies() {
        final Callback<FetchedMoviesList> callback = new Callback<FetchedMoviesList>() {
            @Override
            public void onResponse(final Call<FetchedMoviesList> call, final Response<FetchedMoviesList> response) {
                try {
                    FetchedMoviesList fetchedMoviesList = response.body();
                    addMovieListToDatabase(fetchedMoviesList.getResults(), MoviesProvider.PopularMovies.CONTENT_URI);
                } catch (NullPointerException e) {
                    if (response.code() == 401) {
                        processError("Unauthenticated");
                    } else if (response.code() >= 400) {
                        processError("Client Error " + response.code() + " " + response.message());
                    } else {
                        processError("Network Error");
                    }
                }
            }

            @Override
            public void onFailure(final Call<FetchedMoviesList> call, final Throwable t) {
                processError(t.getMessage());
            }
        };
        fetchMoviesFromTheMovieDb(SORT_BY_POPULAR_PATH, callback);
    }

    private void fetchTopRatedMovies() {
        final Callback<FetchedMoviesList> callback = new Callback<FetchedMoviesList>() {
            @Override
            public void onResponse(final Call<FetchedMoviesList> call, final Response<FetchedMoviesList> response) {
                try {
                    FetchedMoviesList fetchedMoviesList = response.body();
                    addMovieListToDatabase(fetchedMoviesList.getResults(), MoviesProvider.TopRatedMovies.CONTENT_URI);
                } catch (NullPointerException e) {
                    if (response.code() == 401) {
                        processError("Unauthenticated");
                    } else if (response.code() >= 400) {
                        processError("Client Error " + response.code() + " " + response.message());
                    } else {
                        processError("Network Error");
                    }
                }
            }

            @Override
            public void onFailure(final Call<FetchedMoviesList> call, final Throwable t) {
                processError(t.getMessage());
            }
        };
        fetchMoviesFromTheMovieDb(SORT_BY_TOP_RATED_PATH, callback);
    }

    private void processError(final String error) {
        Log.e(LOG_TAG, error);
    }

    /**
     * Take a list of movies and inserts it into a table of the database
     * after deleting its previous content.
     *
     * @param list The list to be stored as popular movies.
     * @param tableUri The Uri where the data must be inserted.
     */
    private void addMovieListToDatabase(List<Movie> list, Uri tableUri) {
        Vector<ContentValues> moviesVector = new Vector<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            Movie selectedMovie = list.get(i);
            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesColumns.MOVIE_ID, selectedMovie.getId());
            movieValues.put(MoviesColumns.TITLE, selectedMovie.getTitle());
            movieValues.put(MoviesColumns.POSTER_PATH, selectedMovie.getPicassoUri());
            movieValues.put(MoviesColumns.OVERVIEW, selectedMovie.getOverview());
            movieValues.put(MoviesColumns.RATING, selectedMovie.getVote_average());
            movieValues.put(MoviesColumns.RELEASE_DATE, selectedMovie.getRelease_date());

            moviesVector.add(movieValues);
        }
        ContentValues[] cvArray = new ContentValues[moviesVector.size()];
        moviesVector.toArray(cvArray);
        getContext().getContentResolver().delete(
                tableUri,
                null,
                null);
        getContext().getContentResolver().bulkInsert(
                tableUri,
                cvArray);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}