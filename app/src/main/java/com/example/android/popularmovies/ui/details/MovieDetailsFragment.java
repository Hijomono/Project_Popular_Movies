package com.example.android.popularmovies.ui.details;

import android.content.Intent;
import android.database.Cursor;
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

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.database.MoviesColumns;
import com.example.android.popularmovies.data.network.FetchedReviewsList;
import com.example.android.popularmovies.data.network.FetchedTrailersList;
import com.example.android.popularmovies.data.network.ServiceProvider;
import com.example.android.popularmovies.data.repository.MoviesRepository;
import com.example.android.popularmovies.data.repository.MoviesStorage;
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
import hugo.weaving.DebugLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "URI";

    private static final int MOVIES_LOADER = 1;
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
    private MoviesRepository moviesRepository;
    private Call<FetchedReviewsList> reviewsCall;
    private Call<FetchedTrailersList> trailersCall;

    @DebugLog
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moviesRepository = new MoviesStorage(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        View rootView;
        if (arguments != null) {
            selectedMovieUri = arguments.getParcelable(MovieDetailsFragment.DETAIL_URI);
            rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
            unbinder = ButterKnife.bind(this, rootView);
        } else {
            rootView = inflater.inflate(R.layout.fragment_movie_details_empty, container, false);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onDestroyView() {
        if (selectedMovieUri != null) {
            unbinder.unbind();
        }
        if (trailersCall != null) {
            trailersCall.cancel();
        }
        if (reviewsCall != null) {
            reviewsCall.cancel();
        }
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details_fragment, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        ShareActionProvider detailShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (trailersAdapter.getCount() != 0) {
            shareItem.setVisible(true);
            detailShareActionProvider.setShareIntent(createShareFirstTrailerIntent());
        } else {
            shareItem.setVisible(false);
        }
    }

    /**
     * Creates an intent to share the youtube URL of the first trailer in the list
     * returned by fetchMovieTrailers.
     *
     * @return an intent to share the first trailer URL.
     */
    private Intent createShareFirstTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailersAdapter.getItem(0).getYoutubeUri().toString());
        return shareIntent;
    }

    /**
     * Calls for trailers for the selected movie and populates trailerListView with them.
     */
    private void fetchMovieTrailers() {
        trailersCall = ServiceProvider.fetchMoviesService().getTrailerList(
                selectedMovie.getId(),
                BuildConfig.THE_MOVIE_DB_API_KEY);
        trailersCall.enqueue(new Callback<FetchedTrailersList>() {
            @Override
            public void onResponse(final Call<FetchedTrailersList> call, final Response<FetchedTrailersList> response) {
                try {
                    // This prevents the screen to scroll to this ListView when it is populated.

                    fetchedTrailersList = response.body();
                    trailerList = fetchedTrailersList.getOnlyTrailers();
                    trailersAdapter.clear();
                    if (trailerList.size() == 0) {
                        trailersHeader.setText(R.string.trailers_header_empty);
                    } else {
                        trailersAdapter.addAll(trailerList);
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
                if (!trailersCall.isCanceled()) {
                    Log.e("getTrailerList threw: ", t.getMessage());
                    trailersHeader.setText(R.string.trailers_header_no_internet);
                }
            }
        });
    }

    /**
     * Calls for reviews for the selected movie and populates reviewListView with them.
     */
    private void fetchMovieReviews() {
        reviewsCall = ServiceProvider.fetchMoviesService().getReviewList(
                selectedMovie.getId(),
                BuildConfig.THE_MOVIE_DB_API_KEY);
        reviewsCall.enqueue(new Callback<FetchedReviewsList>() {
            @Override
            public void onResponse(final Call<FetchedReviewsList> call, final Response<FetchedReviewsList> response) {
                try {
                    fetchedReviewsList = response.body();
                    reviewList = fetchedReviewsList.getResults();
                    reviewsAdapter.clear();
                    if (reviewList.size() == 0) {
                        reviewsHeader.setText(R.string.reviews_header_empty);
                    } else {
                        reviewsAdapter.addAll(reviewList);
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
                if (!reviewsCall.isCanceled()) {
                    Log.e("getReviewList threw: ", t.getMessage());
                    reviewsHeader.setText(R.string.reviews_header_no_internet);
                }
            }
        });
    }

    /**
     * Adds or removes the selected movie from favorites and
     * changes the drawable resource for favButton.
     */
    @OnClick(R.id.detail_fav_button)
    public void onFavButtonClicked() {
        if (selectedMovie.isFavorite(getActivity())) {
            moviesRepository.removeFromFavorites(selectedMovie);
            favButton.setImageResource(R.drawable.fav_off_touch_selector);
        } else {
            moviesRepository.addToFavorites(selectedMovie);
            favButton.setImageResource(R.drawable.fav_on_touch_selector);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != selectedMovieUri) {
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
            selectedMovie = movieOutOfCursor(data);
            displayMovieDetails();
            fillTrailerList();
            fillReviewList();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    /**
     * Creates a {@code Movie} object from a cursor.
     *
     * @param movieCursor the cursor retrieved by the loader.
     */
    private Movie movieOutOfCursor(Cursor movieCursor) {
        return Movie.newBuilder()
                .id(movieCursor.getInt(movieCursor.getColumnIndex(MoviesColumns.MOVIE_ID)))
                .title(movieCursor.getString(movieCursor.getColumnIndex(MoviesColumns.TITLE)))
                .posterPath(movieCursor.getString(movieCursor.getColumnIndex(MoviesColumns.POSTER_PATH)))
                .overview(movieCursor.getString(movieCursor.getColumnIndex(MoviesColumns.OVERVIEW)))
                .voteAverage(movieCursor.getString(movieCursor.getColumnIndex(MoviesColumns.RATING)))
                .releaseDate(movieCursor.getString(movieCursor.getColumnIndex(MoviesColumns.RELEASE_DATE)))
                .build();
    }

    /**
     * Displays the details of the current movie in the UI.
     */
    private void displayMovieDetails() {
        title.setText(selectedMovie.getTitle());
        Picasso.with(getContext()).load(selectedMovie.getPosterPath()).into(poster);
        releaseDate.setText(selectedMovie.getReleaseDate());
        rating.setText(selectedMovie.getRatingOutOfTen());
        overview.setText(selectedMovie.getOverview());
        if (selectedMovie.isFavorite(getActivity())) {
            favButton.setImageResource(R.drawable.fav_on_touch_selector);
        }
    }

    /**
     * Loads the trailers for the current movie, populates the trailers list view with them
     * and adds a click listener to launch them with Youtube.
     */
    private void fillTrailerList() {
        fetchMovieTrailers();
        trailersAdapter = new TrailersAdapter(
                getActivity(),
                new ArrayList<Trailer>());
        trailerListView.setFocusable(false);
        trailerListView.setExpanded(true);
        trailerListView.setAdapter(trailersAdapter);
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Trailer trailerClicked = trailersAdapter.getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, trailerClicked.getYoutubeUri());
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Loads the reviews for the current movie and populates the reviews list view with them.
     */
    private void fillReviewList() {
        fetchMovieReviews();
        reviewsAdapter = new ReviewsAdapter(
                getActivity(),
                new ArrayList<Review>());
        reviewListView.setFocusable(false);
        reviewListView.setExpanded(true);
        reviewListView.setAdapter(reviewsAdapter);
    }
}
