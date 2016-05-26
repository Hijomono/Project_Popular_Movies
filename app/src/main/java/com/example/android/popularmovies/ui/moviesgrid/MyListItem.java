package com.example.android.popularmovies.ui.moviesgrid;

import android.database.Cursor;

import com.example.android.popularmovies.data.database.MoviesColumns;

/**
 * Created by debeyo on 22/05/2016.
 */
public class MyListItem {
    private String posterPath;
    private int movieId;

    private void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    private void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public int getMovieId() {
        return movieId;
    }

    public static MyListItem fromCursor(Cursor cursor) {
        String path = cursor.getString(cursor.getColumnIndex(MoviesColumns.POSTER_PATH));
        int id = cursor.getInt(cursor.getColumnIndex(MoviesColumns._ID));
        MyListItem listItem = new MyListItem();
        listItem.setPosterPath(path);
        listItem.setMovieId(id);
        return listItem;
    }
}
