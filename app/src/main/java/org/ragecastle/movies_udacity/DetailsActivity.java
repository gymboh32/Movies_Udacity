package org.ragecastle.movies_udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.ragecastle.movies_udacity.database.MoviesContract;

/**
 * Created by jahall on 11/11/15.
 *
 * Activity to hold the movie details
 *
 */
public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailsFragment())
                    .commit();
        }
    }

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.favorite_button:
                markAsFavorite();
                Toast.makeText(this, "added to favorites", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void markAsFavorite(){
        ContentValues favoriteValues;
        Intent intent = getIntent();

        favoriteValues = new ContentValues();
        favoriteValues.put(MoviesContract.SortEntry.COLUMN_SORT_BY, "favorite");
        favoriteValues.put(MoviesContract.SortEntry.COLUMN_MOVIE_ID, intent.getStringExtra("movie_id"));

        this.getContentResolver().insert(
                MoviesContract.SortEntry.CONTENT_URI.buildUpon()
                        .appendPath("sort_by")
                        .build(),
                favoriteValues);
    }
}
