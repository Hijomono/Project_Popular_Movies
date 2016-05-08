package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

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
    private Unbinder unbinder;

    private TrailersAdapter trailersAdapter;
    private FetchedTrailersList fetchedTrailersList;
    private List<Trailer> trailerList;
    private ReviewsAdapter reviewsAdapter;
    private FetchedReviewsList fetchedReviewsList;
    private List<Review> reviewList;

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchMovieTrailers();
        fetchMovieReviews();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        trailersAdapter = new TrailersAdapter(
                getActivity(),
                new ArrayList<Trailer>());
        reviewsAdapter = new ReviewsAdapter(
                getActivity(),
                new ArrayList<Review>());

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        Movie movie = ((MovieDetailsActivity) getActivity()).getMovie();
        title.setText(movie.getTitle());
        Picasso.with(getContext()).load(movie.getPicassoUri()).into(poster);
        releaseDate.setText(movie.getRelease_date());
        rating.setText(movie.getRatingOutOfTen());
        overview.setText(movie.getOverview());
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
            detailShareActionProvider.setShareIntent(createShareFirstTrailerIntent());
        } else {
            Toast.makeText(getActivity(), R.string.pref_share_no_trailer_available, Toast.LENGTH_SHORT).show();
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
                ((MovieDetailsActivity) getActivity()).getMovie().getId(),
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
                ((MovieDetailsActivity) getActivity()).getMovie().getId(),
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
}
