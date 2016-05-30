package com.example.android.popularmovies.data.network;

import com.example.android.popularmovies.domain.model.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debeyo on 02/05/2016.
 */
public class FetchedTrailersList {
    List<Trailer> results;

    /**
     * Selects only {@code Trailer} objects with {@code type} "Trailer" so other kind of videos are not shown.
     *
     * @return a {@code Trailer} list whose objects' {@code type} is "Trailer".
     */
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
