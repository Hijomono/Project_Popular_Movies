package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by debeyo on 02/05/2016.
 */
public class ReviewsAdapter extends ArrayAdapter<Review>{

    static class ViewHolder {
        @BindView(R.id.list_item_author_textview)
        TextView authorTextView;
        @BindView(R.id.list_item_review_textview)
        TextView reviewTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public ReviewsAdapter(final Activity context, final List<Review> reviews) {
        super(context,0 , reviews);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final Review review = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(convertView);
        String formatReviewAuthor = "By " +review.getAuthor() + ":";
        viewHolder.authorTextView.setText(formatReviewAuthor);
        viewHolder.reviewTextView.setText(review.getContent());
        return  convertView;
    }
}
