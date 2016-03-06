package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by debeyo on 03/03/2016.
 */
public class Movie implements Parcelable {
    int id;
    String title;
    String posterUrl;
    String plotSynopsis;
    String userRating;
    String releaseDate;

    public Movie(int movieId, String movieTitle, String moviePoster, String moviePlot, String movieRating, String movieRelease) {
        this.id = movieId;
        this.title = movieTitle;
        this.posterUrl = moviePoster;
        this.plotSynopsis = moviePlot;
        this.userRating = movieRating;
        this.releaseDate = movieRelease;
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

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(final Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(final int i) {
            return new Movie[i];
        }
    };
}
