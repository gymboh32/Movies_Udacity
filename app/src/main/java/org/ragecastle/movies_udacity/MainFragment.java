package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
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
        refresh();
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
                refresh();
                return true;
            case R.id.action_sort_by_popular:
                Toast.makeText(getActivity(), "Sorting by Popularity", Toast.LENGTH_LONG).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), POPULAR);
                sharedEditor.apply();
                refresh();
                return true;
            case R.id.action_sort_by_rating:
                Toast.makeText(getActivity(), "Sorting by Rating", Toast.LENGTH_LONG).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), RATING);
                sharedEditor.apply();
                refresh();
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

        Movie[] posterPathArray = {
                new Movie("id",
                        "title",
                        "http://image.tmdb.org/t/p/w500/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg",
                        "release_date",
                        "average_rating",
                        "plot")
        };

        gridView = (GridView) rootView.findViewById(R.id.gridview_posters);

        fillGrid(posterPathArray);

        return rootView;
    }

    private void fillGrid(Movie[] moviePosters){
        posterAdapter = new MoviePosterAdapter(getActivity(), Arrays.asList(moviePosters));
        // Populate grid view
        gridView.setAdapter(posterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the info for the array list item being clicked
                Movie movie = posterAdapter.getItem(position);

                //create new intent to launch the detail page
                // TODO: Just put ID as EXTRA and remove other junk, it will get pulled from database
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("id", true);
                intent.putExtra("title", movie.title);
                intent.putExtra("posterPath", movie.posterPath);
                intent.putExtra("releaseDate", movie.releaseDate);
                intent.putExtra("avgRating", movie.avgRating);
                intent.putExtra("plot", movie.plot);
                startActivity(intent);
            }
        });
    }

    public void refresh() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(getSortBy());
    }

    private String getSortBy() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        return sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort));
    }

}
