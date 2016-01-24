package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ragecastle.movies_udacity.database.MoviesContract;

import java.util.zip.Inflater;

/**
 * Created by jahall on 11/11/15.
 *
 * Fragment to display all the information on the movie
 *
 */
public class DetailsFragment extends Fragment {

    private final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private View rootView;

    public DetailsFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO: try butterkife

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        putDetails();
        putReviews();

        return rootView;
    }

    private void putDetails(){

        String title = "title";
        String image = "image";
        String release_date = "release date";
        String avg_rating = "average rating";
        String plot = "plot";

        final String BASE_URL = "http://image.tmdb.org/t/p/w185";
        Intent intent = getActivity().getIntent();

        TextView titleView = (TextView) rootView.findViewById(R.id.movie_title_text);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.movie_release_text);
        TextView voteAvgView = (TextView) rootView.findViewById(R.id.movie_vote_text);
        TextView plotView = (TextView) rootView.findViewById(R.id.movie_overview_text);

        Cursor cursor;

        String[] projection = {
                MoviesContract.DetailsEntry.COLUMN_MOVIE_ID,
                MoviesContract.DetailsEntry.COLUMN_TITLE,
                MoviesContract.DetailsEntry.COLUMN_IMAGE,
                MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE,
                MoviesContract.DetailsEntry.COLUMN_AVG_RATING,
                MoviesContract.DetailsEntry.COLUMN_PLOT};

        cursor = getActivity().getContentResolver().query(
                MoviesContract.DetailsEntry.CONTENT_URI.buildUpon()
                        .appendPath(intent.getStringExtra("movie_id"))
                        .build(),
                projection,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()){
            do {
                title = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_TITLE));
                image = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_IMAGE));
                release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE));
                avg_rating = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_AVG_RATING));
                plot = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_PLOT));
            } while (cursor.moveToNext());
            cursor.close();
        }

        release_date = "Release Date: \n" + release_date;
        avg_rating = "Average Rating: \n" + avg_rating;

        // Set the url for the poster
        String url = BASE_URL.concat(image);
        // Set the new data to the views
        titleView.setText(title);
        // Get the poster and display it
        // TODO: Add error to display default image
        Picasso.with(getActivity()).load(url).resize(370, 600).into(poster);
        releaseDateView.setText(release_date);
        voteAvgView.setText(avg_rating);
        plotView.setText(plot);
    }

    private void putTrailers(){

        Cursor cursor;
        Intent intent = getActivity().getIntent();
        String review;
        String[] reviewProjection = {MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID,
                MoviesContract.ReviewsEntry.COLUMN_CONTENT};

        cursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI.buildUpon()
                        .appendPath(intent.getStringExtra("movie_id"))
                        .build(),
                reviewProjection,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()){
            do {
                LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.extras_layout);
                TextView textView = new TextView(getActivity());
                review = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_CONTENT));
                textView.setText(review);
                View divider = new View(getActivity());
                divider.setBackgroundColor(0x000000);
                divider.setMinimumHeight(10);
                divider.setMinimumWidth(500);
                linearLayout.addView(divider);
                linearLayout.addView(textView);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void putReviews(){

        Cursor cursor;
        Intent intent = getActivity().getIntent();
        String review;
        String[] reviewProjection = {MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID,
                MoviesContract.ReviewsEntry.COLUMN_CONTENT};

        cursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewsEntry.CONTENT_URI.buildUpon()
                        .appendPath(intent.getStringExtra("movie_id"))
                        .build(),
                reviewProjection,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()){
            do {
                LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.extras_layout);
                TextView textView = new TextView(getActivity());
                review = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_CONTENT));
                textView.setText(review);
                linearLayout.addView(textView);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}

