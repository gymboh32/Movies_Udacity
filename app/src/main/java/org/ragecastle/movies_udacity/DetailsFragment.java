package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ragecastle.movies_udacity.adapters.Movie;
import org.ragecastle.movies_udacity.database.MoviesContract;

/**
 * Created by jahall on 11/11/15.
 *
 * Fragment to display all the information on the movie
 *
 */
public class DetailsFragment extends Fragment {

//    private final String LOG_TAG = DetailsFragment.class.getSimpleName();

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

        String title = "title";
        String image = "image";
        String release_date = "release date";
        String avg_rating = "average rating";
        String plot = "plot";

        final String BASE_URL = "http://image.tmdb.org/t/p/w185";
        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.movie_title_text);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.movie_release_text);
        TextView voteAvgView = (TextView) rootView.findViewById(R.id.movie_vote_text);
        TextView plotView = (TextView) rootView.findViewById(R.id.movie_overview_text);

        Movie movie = getMovie(intent.getStringExtra("movie_id"));
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

        if (cursor.moveToFirst()){
            do {
                title = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_TITLE));
                image = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_IMAGE));
                release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE));
                avg_rating = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_AVG_RATING));
                plot = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_PLOT));
            } while (cursor.moveToNext());
            cursor.close();
        }


//        String title = movie.title;
//        String image = image;
        release_date = "Release Date: \n" + release_date;
        avg_rating = "Average Rating: \n" + avg_rating;
//        String plot = movie.plot;

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
        return rootView;
    }

    private Movie getMovie(String movieId){
        Cursor cursor;

        String[] projection = {
                MoviesContract.DetailsEntry.COLUMN_MOVIE_ID,
                MoviesContract.DetailsEntry.COLUMN_TITLE,
                MoviesContract.DetailsEntry.COLUMN_IMAGE,
                MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE,
                MoviesContract.DetailsEntry.COLUMN_AVG_RATING,
                MoviesContract.DetailsEntry.COLUMN_PLOT};

        cursor = getActivity().getContentResolver().query(
                MoviesContract.DetailsEntry.CONTENT_URI.buildUpon().appendPath(movieId).build(),
                projection,
                null,
                null,
                null);

        if (cursor.moveToFirst()){
            Movie movie;
            do {
                String title = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_TITLE));
                String image = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_IMAGE));
                String release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE));
                String avg_rating = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_AVG_RATING));
                String plot = cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_PLOT));

                movie = new Movie(movieId, image);
            } while (cursor.moveToNext());

            cursor.close();
            return movie;
        }

        return new Movie("id", "title");
    }
}

