package com.example.android.popularmovies.ui.details;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.domain.model.Trailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by debeyo on 02/05/2016.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer>{

    static class ViewHolder {
        @BindView(R.id.list_item_trailer_textview)
        TextView trailerTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public TrailersAdapter(final Activity context, final List<Trailer> trailers) {
        super(context,0 , trailers);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(convertView);
        viewHolder.trailerTextView.setText(trailer.getName());
        return  convertView;
    }
}
