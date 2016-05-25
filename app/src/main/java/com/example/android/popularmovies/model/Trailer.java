package com.example.android.popularmovies.model;

import android.net.Uri;

/**
 * Created by debeyo on 01/05/2016.
 */
public class Trailer {

    private static final String BASE_YOUTUBE_URL = "https://m.youtube.com/watch?v=";

    private final String name;
    private final String key;
    private final String type;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    private Trailer(final Builder builder) {
        key = builder.key;
        name = builder.name;
        type = builder.type;
    }

    /**
     * {@code Trailer} builder static inner class.
     */
    public static final class Builder {
        private String key;
        private String name;
        private String type;

        private Builder() {
        }

        /**
         * Returns a {@code Trailer} built from the parameters previously set.
         *
         * @return a {@code Trailer} built with parameters of this {@code Trailer.Builder}
         */
        public Trailer build() {
            return new Trailer(this);
        }
    }

    /**
     * Creates the Uri to watch the {@code Trailer} on Youtube.
     *
     * @return a Uri built with the {@code key} of this {@code Trailer}
     */
    public Uri getYoutubeUri() {
        String trailerUrl = BASE_YOUTUBE_URL + key;
        return Uri.parse(trailerUrl);
    }
}
