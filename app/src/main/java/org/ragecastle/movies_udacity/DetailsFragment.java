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

        final String BASE_URL = "http://image.tmdb.org/t/p/w185";
        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.movie_title_text);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.movie_release_text);
        TextView voteAvgView = (TextView) rootView.findViewById(R.id.movie_vote_text);
        TextView plotView = (TextView) rootView.findViewById(R.id.movie_overview_text);

        Movie movie = getMovie(intent.getStringExtra("movie_id"));
        String title = movie.title;
        String image = movie.image;
        String releaseDate = "Release Date: \n" + movie.releaseDate;
        String avgRating = "Average Rating: \n" + movie.avgRating;
        String plot = movie.plot;

        // Set the url for the poster
        String url = BASE_URL.concat(image);
        // Set the new data to the views
        titleView.setText(title);
        // Get the poster and display it
        // TODO: Add error to display default image
        Picasso.with(getActivity()).load(url).resize(370, 600).into(poster);
        releaseDateView.setText(releaseDate);
        voteAvgView.setText(avgRating);
        plotView.setText(plot);
        return rootView;
    }

    private Movie getMovie(String movieId){
        Cursor cursor;

        String[] projection = {
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
                MoviesContract.MovieEntry.COLUMN_TITLE,
                MoviesContract.MovieEntry.COLUMN_IMAGE,
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                MoviesContract.MovieEntry.COLUMN_AVG_RATING,
                MoviesContract.MovieEntry.COLUMN_PLOT};

        cursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build(),
                projection,
                null,
                null,
                null);

        if (cursor.moveToFirst()){
            Movie movie;
            do {
                String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
                String image = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE));
                String release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
                String avg_rating = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_AVG_RATING));
                String plot = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_PLOT));

                movie = new Movie(
                        movieId,
                        title,
                        image,
                        release_date,
                        avg_rating,
                        plot,
                        null,
                        null);
            } while (cursor.moveToNext());

            cursor.close();
            return movie;
        }

        return new Movie("id",
                "title",
                "/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg",
                "release_date",
                "average_rating",
                "plot",
                "trailer",
                "review");
    }
}

