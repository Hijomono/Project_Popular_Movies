package com.example.android.popularmovies.data.network;

import com.example.android.popularmovies.domain.model.Review;

import java.util.List;

/**
 * Created by debeyo on 02/05/2016.
 */
public class FetchedReviewsList {
    List<Review> results;

    public List<Review> getResults() {
        return results;
    }
}
