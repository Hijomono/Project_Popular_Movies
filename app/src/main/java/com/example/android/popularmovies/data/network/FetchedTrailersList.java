package com.example.android.popularmovies.data.network;

import com.example.android.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debeyo on 02/05/2016.
 */
public class FetchedTrailersList {
    List<Trailer> results;

    public List<Trailer> getResults() {
        return results;
    }

    public List<Trailer> getOnlyTrailers() {
        List<Trailer> filteredList = new ArrayList<>();
        for (Trailer trailer : results) {
            if (trailer.getType().equals("Trailer")) {
                filteredList.add(trailer);
            }
        }
        return filteredList;
    }
}
