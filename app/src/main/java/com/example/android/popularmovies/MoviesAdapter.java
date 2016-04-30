package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by debeyo on 03/03/2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    static class ViewHolder {
        @BindView(R.id.list_item_poster)
        ImageView moviePosterView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MoviesAdapter(final Activity context, final List<Movie> movies) {
        super(context,0 , movies);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(convertView);
        Picasso.with(getContext()).load(movie.getPicassoUri()).into(viewHolder.moviePosterView);
        return  convertView;
    }
}
