package com.example.android.popularmovies.ui.moviesgrid;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by debeyo on 13/05/2016.
 */
public class MoviesCursorAdapter extends CursorRecyclerViewAdapter<MoviesCursorAdapter.ViewHolder> {

    private final MovieSelectedListener listener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_item_poster)
        ImageView moviePosterView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public MoviesCursorAdapter(Context context, Cursor c, final MovieSelectedListener listener) {
        super(context, c);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final MyListItem myListItem = MyListItem.fromCursor(cursor);
        viewHolder.moviePosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                listener.makeCallback(myListItem.getMovieId());
            }
        });
        Picasso.with(viewHolder.itemView.getContext())
                .load(myListItem.getPosterPath())
                .into(viewHolder.moviePosterView);
    }
}
