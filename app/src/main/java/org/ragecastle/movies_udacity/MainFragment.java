package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.ragecastle.movies_udacity.adapters.Movie;
import org.ragecastle.movies_udacity.adapters.MoviePosterAdapter;
import org.ragecastle.movies_udacity.database.MoviesContract;

import java.util.Arrays;

/**
 * Created by jahall on 11/1/15.
 *
 * Main Fragment to display grid view of movie posters
 * also makes the api call to get all the information needed
 *
 */
public class MainFragment extends Fragment {

//    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private MoviePosterAdapter posterAdapter;
    private GridView gridView;

    public MainFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We has menu options
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        final String POPULAR = "popularity.desc";
        final String RATING = "vote_average.desc";

        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_sort_by_popular:
                Toast.makeText(getActivity(), "Sorting by Popularity", Toast.LENGTH_LONG).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), POPULAR);
                sharedEditor.apply();
                return true;
            case R.id.action_sort_by_rating:
                Toast.makeText(getActivity(), "Sorting by Rating", Toast.LENGTH_LONG).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), RATING);
                sharedEditor.apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // Create the rootView of the Fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridview_posters);
        fillGrid(getImages());
        return rootView;
    }


    private Movie[] getImages(){
        Cursor cursor;

        String[] projection = {
                MoviesContract.MovieEntry.COLUMN_IMAGE,
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID};

        cursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        if (cursor.moveToFirst()){
            Movie[] movieArray = new Movie[cursor.getCount()];
            do {
                String movieId =
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));
                String image =
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_IMAGE));

                movieArray[cursor.getPosition()] =
                        new Movie(
                                movieId,
                                "title",
                                image,
                                "release_date",
                                "avg_Rating",
                                "plot",
                                "trailer",
                                "reviews");
            } while (cursor.moveToNext());

            cursor.close();
            return movieArray;
        }

        cursor.close();
        return new Movie[]{
                new Movie("id",
                        "title",
                        "/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg",
                        "release_date",
                        "average_rating",
                        "plot",
                        "trailer",
                        "reviews")
        };
    }
    private void fillGrid(Movie[] moviePosters){
        posterAdapter = new MoviePosterAdapter(getActivity(), Arrays.asList(moviePosters));
        // Populate grid view
        gridView.setAdapter(posterAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = posterAdapter.getItem(position);
                //create new intent to launch the detail page
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("movie_id", movie.id);
                startActivity(intent);
            }
        });
    }



}
