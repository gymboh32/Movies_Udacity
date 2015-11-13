package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        String title = "movie_title";
        String url = "";
        String releaseDate = "release_date";
        String overview = "plot";
        String avgRating = "average_rating";
        String posterPath;
        final String BASE_URL = "http://image.tmdb.org/t/p/w185";

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra("id")) {
            // Get movie data sent as extra text
            title = intent.getStringExtra("title");
            posterPath = intent.getStringExtra("posterPath");
            releaseDate = "Release Date: \n" + intent.getStringExtra("releaseDate");
            avgRating = "Average Rating: \n" + intent.getStringExtra("avgRating");
            overview = intent.getStringExtra("plot");

            // Set the url for the poster
            url = BASE_URL.concat(posterPath);
        }

        TextView titleView = (TextView) rootView.findViewById(R.id.movie_title_text);
        ImageView poster = (ImageView) rootView.findViewById(R.id.poster_image);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.movie_release_text);
        TextView voteAvgView = (TextView) rootView.findViewById(R.id.movie_vote_text);
        TextView plotView = (TextView) rootView.findViewById(R.id.movie_overview_text);

        // Set the new data to the views
        titleView.setText(title);
        // Get the poster and display it
        Picasso.with(getActivity()).load(url).resize(370, 600).into(poster);
        releaseDateView.setText(releaseDate);
        voteAvgView.setText(avgRating);
        plotView.setText(overview);

        return rootView;
    }
}

