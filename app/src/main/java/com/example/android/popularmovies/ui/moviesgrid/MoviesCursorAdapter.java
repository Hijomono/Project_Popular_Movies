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
    private int selectedMovieId;
    private View selectedView;

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
        if (selectedMovieId == myListItem.getMovieId()) {
            select(viewHolder.itemView);
        } else if (viewHolder.itemView == selectedView){
            unSelect(viewHolder.itemView);
        }
        viewHolder.moviePosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                makeCLickedItemSelected(v);
                selectedMovieId = myListItem.getMovieId();
                listener.makeCallback(selectedMovieId);
            }
        });
        Picasso.with(viewHolder.itemView.getContext())
                .load(myListItem.getPosterPath())
                .into(viewHolder.moviePosterView);
    }

    private void makeCLickedItemSelected(View view) {
        if (selectedView != null) {
            unSelect(selectedView);
        }
       select(view);
    }

    private void select(final View view) {
        selectedView = view;
        selectedView.setPadding(10, 10, 10, 10);
    }

    private void unSelect(final View view) {
        selectedView.setPadding(0, 0, 0, 0);
        selectedView = null;
    }
}
