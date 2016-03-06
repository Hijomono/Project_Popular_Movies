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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        Movie movie = ((MovieDetailsActivity) getActivity()).getMovie();
        ((TextView) rootView.findViewById(R.id.detail_title)).setText(movie.title);
        ImageView posterView = ((ImageView) rootView.findViewById(R.id.detail_poster));
        Uri url = Uri.parse(movie.posterUrl);
        Picasso.with(getContext()).load(url).into(posterView);
        ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movie.releaseDate);
        String finalRating = movie.userRating + "/10";
        ((TextView) rootView.findViewById(R.id.detail_rating)).setText(finalRating);
        ((TextView) rootView.findViewById(R.id.detail_overview)).setText(movie.plotSynopsis);

        return rootView;
    }
}
