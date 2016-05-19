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
import android.widget.FrameLayout;

import com.example.android.popularmovies.ui.moviesgrid.FavoriteMoviesFragment;
import com.example.android.popularmovies.ui.details.MovieDetailsActivity;
import com.example.android.popularmovies.ui.details.MovieDetailsFragment;
import com.example.android.popularmovies.ui.moviesgrid.MoviesGridFragment;
import com.example.android.popularmovies.ui.moviesgrid.PopularMoviesFragment;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.ui.settings.SettingsActivity;
import com.example.android.popularmovies.ui.moviesgrid.TopRatedMoviesFragment;
import com.example.android.popularmovies.data.sync.MoviesSyncAdapter;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements MoviesGridFragment.Callback {

    private static final String SORT_BY_FAVORITE = "favorite";
    private static final String SORT_BY_POPULARITY = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";

    @BindView(R.id.container)
    protected FrameLayout container;
    private String mSortOrder;
    private boolean useTabletLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortOrder = getSortOrder();
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            useTabletLayout = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new MovieDetailsFragment(), null)
                        .commit();
            }
        } else {
            useTabletLayout = false;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, getFragment(mSortOrder), null).commit();

        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.launchSettingsIntent(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String sortOrder = getSortOrder();
        // update the location in our second pane using the fragment manager
        if (!sortOrder.equals(mSortOrder)) {
            mSortOrder = sortOrder;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, getFragment(sortOrder), null).commit();
            if (useTabletLayout) {
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, new MovieDetailsFragment(), null).commit();
            }
        }
    }

    private String getSortOrder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
    }

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
