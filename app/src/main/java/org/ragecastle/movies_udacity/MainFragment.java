package org.ragecastle.movies_udacity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
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
public class MainFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    private final static int LOADER_ID = 0;
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
        final String FAVORITES = "favorite";

        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(getActivity(), "Refreshing", Toast.LENGTH_SHORT).show();
                refresh();
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                return true;
            case R.id.action_sort_by_popular:
                Toast.makeText(getActivity(), "Sorting by Popularity", Toast.LENGTH_SHORT).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), POPULAR);
                sharedEditor.apply();
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                return true;
            case R.id.action_sort_by_rating:
                Toast.makeText(getActivity(), "Sorting by Rating", Toast.LENGTH_SHORT).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), RATING);
                sharedEditor.apply();
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                return true;
            case R.id.action_sort_by_favorite:
                Toast.makeText(getActivity(), "Sorting by Favorite", Toast.LENGTH_SHORT).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), FAVORITES);
                sharedEditor.apply();
                getLoaderManager().restartLoader(LOADER_ID, null, this);
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
        posterAdapter = new MoviePosterAdapter(getActivity(), null, 0);
        gridView.setAdapter(posterAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String movieId;
                if (cursor != null) {
                    // get the movie id to pass to the next screen
                    movieId = cursor.getString(
                            cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_MOVIE_ID));
                    //create new intent to launch the detail page
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("movie_id", movieId);
                    startActivity(intent);
                    // TODO: Replace with fragment manager
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        refresh();
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String movie_id);
    }

    public void refresh() {
        FetchDataTask fetchMoviesTask = new FetchDataTask(getActivity());
        fetchMoviesTask.execute();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private String getSortBy() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        return sharedPref.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort));
    }

    @Override
    public Loader<Cursor> onCreateLoader (int id, Bundle args){

        Uri uri = MoviesContract.SortEntry.CONTENT_URI.buildUpon()
                .appendPath("sort_by")
                .build();

        String [] projection = {
                "details.movie_id",
                "details.image",
                MoviesContract.SortEntry.COLUMN_SORT_BY};

        String selection = MoviesContract.SortEntry.COLUMN_SORT_BY;

        String [] selectionArgs = {getSortBy()};

        return new CursorLoader(getContext(),
                uri,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (posterAdapter != null && cursor != null) {
            posterAdapter.swapCursor(cursor);
        }
        else {
            Log.e(LOG_TAG, "adapter or cursor broke");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        posterAdapter.swapCursor(null);
    }
}
