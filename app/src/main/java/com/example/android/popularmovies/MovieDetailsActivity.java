package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String MOVIE_EXTRA = "MovieDetailsActivity.MOVIE_EXTRA";
    private Movie chosenMovie;

    public static Intent launchDetailsIntent(final Movie movie, final Context context) {
        final Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra(MOVIE_EXTRA, movie);
        return intent;
    }

    public Movie getMovie() {
        return chosenMovie;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle movieBundle = getIntent().getExtras();
        chosenMovie = movieBundle.getParcelable(MOVIE_EXTRA);

        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
