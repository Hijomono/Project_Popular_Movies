package com.example.android.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by debeyo on 03/03/2016.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    public MoviesAdapter(final Activity context, final List<Movie> movies) {
        super(context,0 , movies);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.list_item_poster);

        Uri url = Uri.parse(movie.posterUrl);
        Picasso.with(getContext()).load(url).into(posterView);

        return  convertView;
    }
}
