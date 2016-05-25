package com.example.android.popularmovies.model;

/**
 * Created by debeyo on 01/05/2016.
 */
public class Review {

    private final String author;
    private final String content;

    private Review(final Builder builder) {
        author = builder.author;
        content = builder.content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    /**
     * {@code Review} builder static inner class.
     */
    public static final class Builder {
        private String author;
        private String content;

        private Builder() {
        }

        /**
         * Sets the {@code author} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code author} to set
         * @return a reference to this Builder
         */
        public Builder author(final String val) {
            author = val;
            return this;
        }

        /**
         * Sets the {@code content} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code content} to set
         * @return a reference to this Builder
         */
        public Builder content(final String val) {
            content = val;
            return this;
        }

        /**
         * Returns a {@code Review} built from the parameters previously set.
         *
         * @return a {@code Review} built with parameters of this {@code Review.Builder}
         */
        public Review build() {
            return new Review(this);
        }
    }
}
