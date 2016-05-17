package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.database.MoviesDatabase;
import com.example.android.popularmovies.data.database.MoviesProvider;
import com.example.android.popularmovies.data.network.FetchedReviewsList;
import com.example.android.popularmovies.data.network.FetchedTrailersList;
import com.example.android.popularmovies.data.network.ServiceProvider;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final int MOVIES_LOADER = 0;
    private static final String[] MOVIES_COLUMNS = {
            MoviesColumns._ID,
            MoviesColumns.MOVIE_ID,
            MoviesColumns.TITLE,
            MoviesColumns.POSTER_PATH,
            MoviesColumns.OVERVIEW,
            MoviesColumns.RATING,
            MoviesColumns.RELEASE_DATE
    };

    @BindView(R.id.detail_scrollview)
    ScrollView scrollView;
    @BindView(R.id.detail_title)
    TextView title;
    @BindView(R.id.detail_poster)
    ImageView poster;
    @BindView(R.id.detail_release_date)
    TextView releaseDate;
    @BindView(R.id.detail_rating)
    TextView rating;
    @BindView(R.id.detail_overview)
    TextView overview;
    @BindView(R.id.detail_trailers_header)
    TextView trailersHeader;
    @BindView(R.id.detail_trailer_list)
    com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView trailerListView;
    @BindView(R.id.detail_reviews_header)
    TextView reviewsHeader;
    @BindView(R.id.detail_review_list)
    com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView reviewListView;
    @BindView(R.id.detail_fav_button)
    ImageView favButton;
    private Unbinder unbinder;

    private TrailersAdapter trailersAdapter;
    private FetchedTrailersList fetchedTrailersList;
    private List<Trailer> trailerList;
    private ReviewsAdapter reviewsAdapter;
    private FetchedReviewsList fetchedReviewsList;
    private List<Review> reviewList;
    private Movie selectedMovie;
    private Uri selectedMovieUri;

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            selectedMovieUri = arguments.getParcelable(MovieDetailsFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details_fragment, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider detailShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (trailersAdapter.getCount() != 0) {
            shareItem.setVisible(true);
            detailShareActionProvider.setShareIntent(createShareFirstTrailerIntent());
        } else {
            shareItem.setVisible(false);
        }
    }

    private Intent createShareFirstTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailersAdapter.getItem(0).getYoutubeUri().toString());
        return shareIntent;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Calls for trailers for the selected movie and populates trailerListView with them.
     */
    private void fetchMovieTrailers() {
        Call<FetchedTrailersList> trailersCall;
        trailersCall = ServiceProvider.fetchMoviesService().getTrailerList(
                selectedMovie.getId(),
                BuildConfig.THE_MOVIE_DB_API_KEY);
        trailersCall.enqueue(new Callback<FetchedTrailersList>() {
            @Override
            public void onResponse(final Call<FetchedTrailersList> call, final Response<FetchedTrailersList> response) {
                try {
                    // This prevents the screen to scroll to this ListView when it is populated.
                    trailerListView.setFocusable(false);
                    fetchedTrailersList = response.body();
                    trailerList = fetchedTrailersList.getOnlyTrailers();
                    trailersAdapter.clear();
                    if (trailerList.size() == 0) {
                        trailersHeader.setText(R.string.trailers_header_empty);
                    } else {
                        trailersAdapter.addAll(trailerList);
                        trailerListView.setExpanded(true);
                    }
                } catch (NullPointerException e) {
                    Toast toast = null;
                    if (response.code() == 401) {
                        toast = Toast.makeText(getActivity(), "Unauthenticated", Toast.LENGTH_SHORT);
                    } else if (response.code() >= 400) {
                        toast = Toast.makeText(getActivity(), "Client Error " + response.code()
                                + " " + response.message(), Toast.LENGTH_SHORT);
                    }
                    toast.show();
                }
                // This is called here so trailersAdapter.getItem(0)
                // returns the trailer needed in createFirstTrailerIntent.
                setHasOptionsMenu(true);
            }

            @Override
            public void onFailure(final Call<FetchedTrailersList> call, final Throwable t) {
                Log.e("getTrailerList threw: ", t.getMessage());
            }
        });
    }

    /**
     * Calls for reviews for the selected movie and populates reviewListView with them.
     */
    private void fetchMovieReviews() {
        Call<FetchedReviewsList> reviewsCall;
        reviewsCall = ServiceProvider.fetchMoviesService().getReviewList(
                selectedMovie.getId(),
                BuildConfig.THE_MOVIE_DB_API_KEY);
        reviewsCall.enqueue(new Callback<FetchedReviewsList>() {
            @Override
            public void onResponse(final Call<FetchedReviewsList> call, final Response<FetchedReviewsList> response) {
                try {
                    // This prevents the screen to scroll to this ListView when it is populated.
                    reviewListView.setFocusable(false);
                    fetchedReviewsList = response.body();
                    reviewList = fetchedReviewsList.getResults();
                    reviewsAdapter.clear();
                    if (reviewList.size() == 0) {
                        reviewsHeader.setText(R.string.reviews_header_empty);
                    } else {
                        reviewsAdapter.addAll(reviewList);
                        reviewListView.setExpanded(true);
                    }
                } catch (NullPointerException e) {
                    Toast toast = null;
                    if (response.code() == 401) {
                        toast = Toast.makeText(getActivity(), "Unauthenticated", Toast.LENGTH_SHORT);
                    } else if (response.code() >= 400) {
                        toast = Toast.makeText(getActivity(), "Client Error " + response.code()
                                + " " + response.message(), Toast.LENGTH_SHORT);
                    }
                    toast.show();
                }
            }

            @Override
            public void onFailure(final Call<FetchedReviewsList> call, final Throwable t) {
                Log.e("getReviewList threw: ", t.getMessage());
            }
        });
    }

    /**
     * Calls favMovie or unfavMovie depending on the current movie being favorite or not
     * and changes the drawable resource for favButton.
     */
    @OnClick(R.id.detail_fav_button)
    public void onFavButtonClicked() {
        if (selectedMovie.isFavorite(getActivity())) {
            unfavMovie();
            favButton.setImageResource(R.drawable.fav_off_touch_selector);
        } else {
            favMovie();
            favButton.setImageResource(R.drawable.fav_on_touch_selector);
        }
    }

    /**
     * Adds the movie to the favorite movies database.
     */
    private void favMovie() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesColumns.MOVIE_ID, selectedMovie.getId());
        movieValues.put(MoviesColumns.TITLE, selectedMovie.getTitle());
        movieValues.put(MoviesColumns.POSTER_PATH, selectedMovie.getPoster_path());
        movieValues.put(MoviesColumns.OVERVIEW, selectedMovie.getOverview());
        movieValues.put(MoviesColumns.RATING, selectedMovie.getVote_average());
        movieValues.put(MoviesColumns.RELEASE_DATE, selectedMovie.getRelease_date());
        getActivity().getContentResolver().insert(
                MoviesProvider.FavoriteMovies.CONTENT_URI,
                movieValues);
    }

    /**
     * Removes the movie from the favorite movies database.
     */
    private void unfavMovie() {
        getActivity().getContentResolver().delete(
                MoviesProvider.FavoriteMovies.CONTENT_URI,
                MoviesColumns.MOVIE_ID + "=?",
                new String[]{String.valueOf(selectedMovie.getId())}
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != selectedMovieUri) {
// Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    selectedMovieUri,
                    MOVIES_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            selectedMovie = Movie.newBuilder()
                    .id(data.getInt(data.getColumnIndex(MoviesColumns.MOVIE_ID)))
                    .title(data.getString(data.getColumnIndex(MoviesColumns.TITLE)))
                    .poster_path(data.getString(data.getColumnIndex(MoviesColumns.POSTER_PATH)))
                    .overview(data.getString(data.getColumnIndex(MoviesColumns.OVERVIEW)))
                    .vote_average(data.getString(data.getColumnIndex(MoviesColumns.RATING)))
                    .release_date(data.getString(data.getColumnIndex(MoviesColumns.RELEASE_DATE)))
                    .build();

            fetchMovieTrailers();
            fetchMovieReviews();

            trailersAdapter = new TrailersAdapter(
                    getActivity(),
                    new ArrayList<Trailer>());
            reviewsAdapter = new ReviewsAdapter(
                    getActivity(),
                    new ArrayList<Review>());

            title.setText(selectedMovie.getTitle());
            Picasso.with(getContext()).load(selectedMovie.getPoster_path()).into(poster);
            releaseDate.setText(selectedMovie.getRelease_date());
            rating.setText(selectedMovie.getRatingOutOfTen());
            overview.setText(selectedMovie.getOverview());
            if (selectedMovie.isFavorite(getActivity())) {
                favButton.setImageResource(R.drawable.fav_on_touch_selector);
            }
            trailerListView.setAdapter(trailersAdapter);
            trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    final Trailer trailerClicked = trailersAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW, trailerClicked.getYoutubeUri());
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.d(LOG_TAG, "Couldn't call " + trailerClicked.getYoutubeUri() + ", no receiving apps installed!");
                    }
                }
            });
            reviewListView.setAdapter(reviewsAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private boolean checkFavorite(int movieId) {
        final SQLiteDatabase db = com.example.android.popularmovies.data.provider.MoviesDatabase.getInstance(getActivity()).getReadableDatabase();
        return DatabaseUtils.longForQuery(
                db,
                "select count(*) from "
                        + MoviesDatabase.FAVORITE_MOVIES
                        + " where "
                        + MoviesColumns.MOVIE_ID
                        + "=? limit 1",
                new String[]{String.valueOf(movieId)}
        ) > 0;
    }
}
