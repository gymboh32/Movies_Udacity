package org.ragecastle.movies_udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


    // TODO: change text of favorite button if already marked as favorite
    // TODO: change onClickListener if already in database

    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.favorite_button:
                markAsFavorite();
                break;
        }
    }

    private void markAsFavorite(){
        ContentValues favoriteValues;
        Intent intent = getIntent();
        Cursor cursor;
        String[] projection = {MoviesContract.SortEntry.COLUMN_MOVIE_ID,
                MoviesContract.SortEntry.COLUMN_SORT_BY};

        cursor = getContentResolver().query(
                MoviesContract.SortEntry.CONTENT_URI.buildUpon()
                        .appendPath(intent.getStringExtra("movie_id"))
                        .build(),
                projection,
                null,
                null,
                null
        );

        Boolean isFavorite = false;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                isFavorite =
                        cursor.getString(
                                cursor.getColumnIndex(
                                        MoviesContract.SortEntry.COLUMN_SORT_BY))
                                .contentEquals("favorite") || isFavorite;
                // TODO: Remove Log
//                Log.e(LOG_TAG, cursor.getString(cursor.getColumnIndex(MoviesContract.SortEntry.COLUMN_SORT_BY)));

            } while (cursor.moveToNext());
        }
        cursor.close();

        if (isFavorite) {
            // TODO: Remove from favorites
//            getContentResolver().delete(
//                MoviesContract.SortEntry.CONTENT_URI.buildUpon()
//                        .appendPath(intent.getStringExtra("movie_id"))
//                        .build(),
//                    MoviesContract.SortEntry.COLUMN_MOVIE_ID,
//                null);

            Toast.makeText(this, "Removing from favorites is in the works!", Toast.LENGTH_SHORT).show();
        } else {
            favoriteValues = new ContentValues();
            favoriteValues.put(MoviesContract.SortEntry.COLUMN_SORT_BY,
                    "favorite");
            favoriteValues.put(MoviesContract.SortEntry.COLUMN_MOVIE_ID,
                    intent.getStringExtra("movie_id"));

            this.getContentResolver().insert(
                    MoviesContract.SortEntry.CONTENT_URI.buildUpon()
                            .appendPath("sort_by")
                            .build(),
                    favoriteValues);

            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
    }
}