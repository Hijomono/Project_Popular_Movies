package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MovieDetailsActivity extends AppCompatActivity {

    public Movie chosenMovie;

    public static Intent launchDetailsIntent(final Movie movie, final Context context) {
        final Intent intent = new Intent(context, MovieDetailsActivity.class);
        intent.putExtra("com.package.Movie", movie);
        return intent;
    }

    public Movie getMovie() {
        return chosenMovie;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle movieBundle = getIntent().getExtras();
        chosenMovie = movieBundle.getParcelable("com.package.Movie");



        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
