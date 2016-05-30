package com.example.android.popularmovies.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.sync.MoviesSyncAdapter;
import com.example.android.popularmovies.ui.details.MovieDetailsActivity;
import com.example.android.popularmovies.ui.details.MovieDetailsFragment;
import com.example.android.popularmovies.ui.moviesgrid.FavoriteMoviesFragment;
import com.example.android.popularmovies.ui.moviesgrid.MoviesGridFragment;
import com.example.android.popularmovies.ui.moviesgrid.PopularMoviesFragment;
import com.example.android.popularmovies.ui.moviesgrid.TopRatedMoviesFragment;
import com.example.android.popularmovies.ui.settings.SettingsActivity;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesGridFragment.Callback {

    private static final String SORT_BY_FAVORITE = "favorite";
    private static final String SORT_BY_POPULARITY = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";
    private static final String FRAGMRNT_KEY = "gridFragment";

    private String mSortOrder;
    private boolean useTabletLayout;
    private Fragment gridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortOrder = getSortOrder();
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.detail_container) != null) {
            useTabletLayout = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new MovieDetailsFragment(), null)
                        .commit();
            }
        } else {
            useTabletLayout = false;
        }
        setSupportActionBar(ButterKnife.<Toolbar>findById(this, R.id.toolbar));
        if(savedInstanceState == null) {
            gridFragment = getFragment(mSortOrder);
        } else {
            gridFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMRNT_KEY);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, gridFragment, null).commit();

        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String sortOrder = getSortOrder();
        if (!sortOrder.equals(mSortOrder)) {
            mSortOrder = sortOrder;
            gridFragment = getFragment(mSortOrder);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, gridFragment, null).commit();
            if (useTabletLayout) {
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, new MovieDetailsFragment(), null).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getSupportFragmentManager() == gridFragment.getFragmentManager()) {
            getSupportFragmentManager().putFragment(outState, FRAGMRNT_KEY, gridFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.launchSettingsIntent(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets the current sort order from preferences.
     *
     * @return the selection of movies to be shown.
     */
    private String getSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
    }

    /**
     * Creates an instance of one of the subclasses of MoviesGridFragment depending on the sort order.
     *
     * @param  sortOrder the current sort order.
     * @return a fragment that matches the sort order.
     */
    private Fragment getFragment(final String sortOrder) {
        switch (sortOrder) {
            case SORT_BY_FAVORITE:
                return new FavoriteMoviesFragment();
            case SORT_BY_POPULARITY:
                return new PopularMoviesFragment();
            case SORT_BY_TOP_RATED:
                return new TopRatedMoviesFragment();
            default:
                return null;
        }
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (useTabletLayout) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailsFragment.DETAIL_URI, movieUri);
            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, null)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }
}
