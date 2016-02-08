package org.ragecastle.movies_udacity;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.ragecastle.movies_udacity.adapters.Movie;
import org.ragecastle.movies_udacity.database.MoviesContract;
import org.w3c.dom.Text;

import java.awt.font.TextAttribute;
import java.net.URI;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by jahall on 11/11/15.
 *
 * Fragment to display all the information on the movie
 *
 */
public class DetailsFragment extends Fragment {

    // TODO: find out why loaderCallbacks<Cursor> had worse performance than using multiple cursors.

    private final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private View rootView;
    private LinearLayout extrasLayout;

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
        // TODO: add share intent to share trailer link
        // TODO: make trailers and reviews more presentable

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        extrasLayout = (LinearLayout) rootView.findViewById(R.id.extras_layout);

        Cursor detailsCursor = getCursor(MoviesContract.DetailsEntry.CONTENT_URI,
                new String[] {MoviesContract.DetailsEntry.COLUMN_TITLE,
                        MoviesContract.DetailsEntry.COLUMN_IMAGE,
                        MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE,
                        MoviesContract.DetailsEntry.COLUMN_AVG_RATING,
                        MoviesContract.DetailsEntry.COLUMN_PLOT});

        Cursor trailersCursor = getCursor(MoviesContract.TrailersEntry.CONTENT_URI,
                new String[]{MoviesContract.TrailersEntry.COLUMN_SITE,
                        MoviesContract.TrailersEntry.COLUMN_NAME,
                        MoviesContract.TrailersEntry.COLUMN_TRAILER_KEY});

        Cursor reviewsCursor = getCursor(MoviesContract.ReviewsEntry.CONTENT_URI,
                new String[]{MoviesContract.ReviewsEntry.COLUMN_CONTENT,
                        MoviesContract.ReviewsEntry.COLUMN_URL});

        putDetails(detailsCursor);
        detailsCursor.close();

        putTrailers(trailersCursor);
        trailersCursor.close();

        putReviews(reviewsCursor);
        reviewsCursor.close();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void putDetails(Cursor cursor){

        String title = "title";
        String image = "image";
        String release_date = "release date";
        String avg_rating = "average rating";
        String plot = "plot";

        final String BASE_URL = "http://image.tmdb.org/t/p/w185";

        TextView titleView = (TextView) rootView.findViewById(R.id.movie_title_text);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.movie_release_text);
        TextView voteAvgView = (TextView) rootView.findViewById(R.id.movie_vote_text);
        TextView plotView = (TextView) rootView.findViewById(R.id.movie_overview_text);

        if (cursor != null && cursor.moveToFirst()){
            do {
                title = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_TITLE));
                image = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_IMAGE));
                release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE));
                avg_rating = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_AVG_RATING));
                plot = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_PLOT));
            } while (cursor.moveToNext());
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

    private void putTrailers(Cursor cursor){

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String trailerName = cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_NAME));
                String site = cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_SITE));
                String key = cursor.getString(cursor.getColumnIndex(MoviesContract.TrailersEntry.COLUMN_TRAILER_KEY));

                // Build the URI to pass in for movie information
                final Uri builder = Uri.EMPTY.buildUpon()
                        .scheme("http")
                        .authority(site)
                        .appendPath("watch")
                        .appendQueryParameter("v", key)
                        .build();

                TextView trailerText = new TextView(getActivity());
                trailerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                trailerText.setPadding(0, 5, 0, 5);
                trailerText.setTextSize(2, 20);
                trailerText.setText(trailerName);
                trailerText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //create new intent to launch the trailer
                        Intent intent = new Intent(Intent.ACTION_VIEW, builder);
                        startActivity(intent);
                    }
                });
                extrasLayout.addView(trailerText);
            } while (cursor.moveToNext());
        }
    }

    private void putReviews(Cursor cursor){

        String review;

        if (cursor != null && cursor.moveToFirst()){
            do {
                review = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_CONTENT));
                final String url = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewsEntry.COLUMN_URL));

                TextView reviewText = new TextView(getActivity());
                reviewText.setMaxLines(3);
                reviewText.setPadding(0, 5, 0, 5);
                reviewText.setText(review);
                reviewText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //create new intent to go to review
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
                extrasLayout.addView(reviewText);
           } while (cursor.moveToNext());
        }
    }

    private Cursor getCursor(Uri baseUri, String [] projection){

        Intent intent = getActivity().getIntent();

        Uri uri = baseUri.buildUpon()
                .appendPath(intent.getStringExtra("movie_id"))
                .build();

        return getActivity().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null);
    }
}

