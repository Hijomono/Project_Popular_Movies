package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by debeyo on 03/03/2016.
 */
public class Movie implements Parcelable {
    private final int id;
    private final String title;
    private final String posterUrl;
    private final String plotSynopsis;
    private final String userRating;
    private final String releaseDate;

    private Movie(final Builder builder) {
        id = builder.id;
        title = builder.title;
        posterUrl = builder.posterUrl;
        plotSynopsis = builder.plotSynopsis;
        userRating = builder.userRating;
        releaseDate = builder.releaseDate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(final Movie copy) {
        Builder builder = new Builder();
        builder.id = copy.id;
        builder.title = copy.title;
        builder.posterUrl = copy.posterUrl;
        builder.plotSynopsis = copy.plotSynopsis;
        builder.userRating = copy.userRating;
        builder.releaseDate = copy.releaseDate;
        return builder;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterUrl = in.readString();
        plotSynopsis = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return id+ "--" + title + "--" + posterUrl + "--" + plotSynopsis + "--" + userRating + "--" + releaseDate;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(posterUrl);
        parcel.writeString(plotSynopsis);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(final Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(final int i) {
            return new Movie[i];
        }
    };

    /**
     * {@code Movie} builder static inner class.
     */
    public static final class Builder {
        private int id;
        private String title;
        private String posterUrl;
        private String plotSynopsis;
        private String userRating;
        private String releaseDate;

        private Builder() {
        }

        /**
         * Sets the {@code id} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code id} to set
         * @return a reference to this Builder
         */
        public Builder id(final int val) {
            id = val;
            return this;
        }

        /**
         * Sets the {@code title} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code title} to set
         * @return a reference to this Builder
         */
        public Builder title(final String val) {
            title = val;
            return this;
        }

        /**
         * Sets the {@code posterUrl} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code posterUrl} to set
         * @return a reference to this Builder
         */
        public Builder posterUrl(final String val) {
            posterUrl = val;
            return this;
        }

        /**
         * Sets the {@code plotSynopsis} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code plotSynopsis} to set
         * @return a reference to this Builder
         */
        public Builder plotSynopsis(final String val) {
            plotSynopsis = val;
            return this;
        }

        /**
         * Sets the {@code userRating} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code userRating} to set
         * @return a reference to this Builder
         */
        public Builder userRating(final String val) {
            userRating = val;
            return this;
        }

        /**
         * Sets the {@code releaseDate} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code releaseDate} to set
         * @return a reference to this Builder
         */
        public Builder releaseDate(final String val) {
            releaseDate = val;
            return this;
        }

        /**
         * Returns a {@code Movie} built from the parameters previously set.
         *
         * @return a {@code Movie} built with parameters of this {@code Movie.Builder}
         */
        public Movie build() {
            return new Movie(this);
        }
    }
}
