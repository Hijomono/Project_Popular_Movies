package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.database.MoviesColumns;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by debeyo on 13/05/2016.
 */
public class MoviesCursorAdapter extends CursorAdapter {

    static class ViewHolder {
        @BindView(R.id.list_item_poster)
        ImageView moviePosterView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MoviesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Picasso.with(context)
                .load(cursor.getString(cursor.getColumnIndex(MoviesColumns.POSTER_PATH)))
                .into(viewHolder.moviePosterView);
    }
}
