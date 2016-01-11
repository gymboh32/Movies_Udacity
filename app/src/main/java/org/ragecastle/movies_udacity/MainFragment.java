package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import org.json.JSONException;
import org.ragecastle.movies_udacity.adapters.Movie;
import org.ragecastle.movies_udacity.adapters.MoviePosterAdapter;
import org.ragecastle.movies_udacity.database.MoviesContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by jahall on 11/1/15.
 *
 * Main Fragment to display grid view of movie posters
 * also makes the api call to get all the information needed
 *
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

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

        gridView = (GridView) rootView.findViewById(R.id.gridview_posters);
        refresh();
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

    public void refresh() {
        FetchDataTask fetchMoviesTask = new FetchDataTask();
        fetchMoviesTask.execute(getSortBy());
    }

    private String getSortBy() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        return sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort));
    }

    public class FetchDataTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchDataTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection connection;
            BufferedReader reader = null;
            InputStream inputStream;
            StringBuffer buffer;
            String result = null;

            try {
                // constants of api parameters
                final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
                final String API_KEY_PARAM = "api_key";
                final String SORT_PARAM = "sort_by";
                final String APIKEY = "";

                // Build the URI to pass in for movie information
                Uri builder = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, APIKEY)
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .build();

                // Create URL to pass in for movie information
                URL url = new URL(builder.toString());

                // Open the connection for the HTTP request
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Get the input stream from the URL request
                inputStream = connection.getInputStream();

                // Create buffer to write the input stream to
                buffer = new StringBuffer();
                // If stream is empty return null
                if (inputStream == null) {
                    return null;
                }

                // Read the input stream
                reader = new BufferedReader(new InputStreamReader(inputStream));

                // Write the reader to the buffer as long as there is something to write
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                // Convert the buffer to String to be sent to the Parser
                result = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Check the API Key");
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Couldn't close reader");
                }
            }

            try {
                return MovieParser.getMovieInfo(result);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] moviesArray) {

            fillGrid(moviesArray);
            updateDB(moviesArray);
        }
    }

    public void updateDB(Movie[] moviesArray){
        ContentValues movieValues;

        // Loop through static array of Flavors, add each to an instance of ContentValues
        // in the array of ContentValues
        for (Movie aMoviesArray : moviesArray) {
            movieValues = new ContentValues();
            movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, aMoviesArray.id);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, aMoviesArray.title);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_IMAGE, aMoviesArray.image);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, aMoviesArray.releaseDate);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_AVG_RATING, aMoviesArray.avgRating);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_PLOT, aMoviesArray.plot);

            // array to filter columns being queried
            String[] projection = {MoviesContract.MovieEntry.COLUMN_MOVIE_ID};

            Cursor cursor = getActivity().getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI.buildUpon()
                            .appendPath(aMoviesArray.id)
                            .build(),
                    projection,
                    null,
                    null,
                    null);

            // Check if the value is already in the database before adding it
            if (cursor.getCount() == 0) {
                Log.i(LOG_TAG, "Added " + aMoviesArray.title);
                // add the movie to the database
                getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieValues);
            }
            cursor.close();
        }
    }

}
