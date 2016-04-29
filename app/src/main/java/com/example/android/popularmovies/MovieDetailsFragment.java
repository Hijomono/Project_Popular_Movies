package com.example.android.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

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
    private Unbinder unbinder;


    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        Movie movie = ((MovieDetailsActivity) getActivity()).getMovie();
        title.setText(movie.getTitle());
        Uri url = Uri.parse(movie.getPosterUrl());
        Picasso.with(getContext()).load(url).into(poster);
        releaseDate.setText(movie.getReleaseDate());
        String ratingOutOfTen = movie.getUserRating() + "/10";
        rating.setText(ratingOutOfTen);
        overview.setText(movie.getPlotSynopsis());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
