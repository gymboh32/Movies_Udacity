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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // update the database
        refresh();
    }

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
                fillGrid();
                return true;
            case R.id.action_sort_by_rating:
                Toast.makeText(getActivity(), "Sorting by Rating", Toast.LENGTH_LONG).show();
                sharedEditor.putString(getString(R.string.pref_sort_key), RATING);
                sharedEditor.apply();
                fillGrid();
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
        fillGrid();
        return rootView;
    }

    private void fillGrid(){
        Cursor cursor;
        Movie[] moviePosters;

        // get a list of movie ids
        String [] movieProjection = {MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
                MoviesContract.MovieEntry.COLUMN_SORT_PARAM};

        cursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI.buildUpon()
                        .appendPath("sort_by")
                        .build(),
                movieProjection,
                MoviesContract.MovieEntry.COLUMN_SORT_PARAM,
                new String[]{getSortBy()},
                null);

        String[] movieId = new String[cursor.getCount()];

        if (cursor.moveToFirst()){
            do {
                movieId[cursor.getPosition()] =
                        cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));

            } while (cursor.moveToNext());
            cursor.close();
        }

        // query the movies for the images
        // TODO: query database based on sort by option and build an array of movies
        String [] detailsProjection = {MoviesContract.DetailsEntry.COLUMN_MOVIE_ID,
                MoviesContract.DetailsEntry.COLUMN_IMAGE};

        moviePosters = new Movie [movieId.length];

        for(int i=0;i<movieId.length;i++) {

            cursor = getActivity().getContentResolver().query(
                    MoviesContract.DetailsEntry.CONTENT_URI.buildUpon().appendPath(movieId[i]).build(),
                    detailsProjection,
                    MoviesContract.DetailsEntry.COLUMN_MOVIE_ID,
                    movieId,
                    null);

            if (cursor.moveToFirst()) {
                do {

                    moviePosters[i] = new Movie(
                            cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_MOVIE_ID)),
                            cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_IMAGE)));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

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
        fetchMoviesTask.execute();
//        fillGrid();
    }

    private String getSortBy() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        return sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.pref_default_sort));
    }

    public class FetchDataTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchDataTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            URL popularityURL = null;
            URL ratingURL = null;

            // constants of api parameters
            final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
            final String API_KEY_PARAM = "api_key";
            final String SORT_PARAM = "sort_by";
            final String APIKEY = "";
            final String POPULAR = "popularity.desc";
            final String RATING = "vote_average.desc";

            // Build the URI to pass in for movie information
            Uri builder = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, APIKEY)
                    .appendQueryParameter(SORT_PARAM, POPULAR)
                    .build();

            try{
                // Create URL to pass in for movie information
                popularityURL = new URL(builder.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Check the API Key");
            }

            String moviesByPopularity = getResults(popularityURL);

            try {
                putToDB(moviesByPopularity, POPULAR);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            // Build the URI to pass in for movie information
            builder = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, APIKEY)
                    .appendQueryParameter(SORT_PARAM, RATING)
                    .build();

            try{
                // Create URL to pass in for movie information
                ratingURL = new URL(builder.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Check the API Key");
            }

            String moviesByRating = getResults(ratingURL);

            try {
                putToDB(moviesByRating, RATING);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        private void putToDB(String apiResult, String sortBy) throws JSONException {
            ContentValues movieValues;
            ContentValues detailsValues;

            // Make the movieData parameter a JSONObject
            JSONObject jsonMovieData = new JSONObject(apiResult);

            // Extract the list of results from movieData
            JSONArray movieInfoArray = jsonMovieData.getJSONArray("results");

            // Loop through the JSONArray and extract the poster location information
            for (int i = 0; i < movieInfoArray.length(); i++) {

                // Pull the movieInfo from the Array
                // TODO: Pull the Trailer data
                // TODO: Pull the Reviews Data
                JSONObject movieInfo = movieInfoArray.getJSONObject(i);

                movieValues = new ContentValues();
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieInfo.getString("id"));
                movieValues.put(MoviesContract.MovieEntry.COLUMN_SORT_PARAM, sortBy);

                detailsValues = new ContentValues();
                detailsValues.put(MoviesContract.DetailsEntry.COLUMN_MOVIE_ID,
                        movieInfo.getString("id"));
                detailsValues.put(MoviesContract.DetailsEntry.COLUMN_TITLE,
                        movieInfo.getString("title"));
                detailsValues.put(MoviesContract.DetailsEntry.COLUMN_IMAGE,
                        movieInfo.getString("poster_path"));
                detailsValues.put(MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE,
                        movieInfo.getString("release_date"));
                detailsValues.put(MoviesContract.DetailsEntry.COLUMN_AVG_RATING,
                        movieInfo.getString("vote_average"));
                detailsValues.put(MoviesContract.DetailsEntry.COLUMN_PLOT,
                        movieInfo.getString("overview"));

                // add the movie to the database
                getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI,
                        movieValues);
                getActivity().getContentResolver().insert(MoviesContract.DetailsEntry.CONTENT_URI,
                        detailsValues);
            }
        }

        private String getResults(URL url){

            HttpURLConnection connection;
            BufferedReader reader = null;
            InputStream inputStream;
            StringBuffer buffer;

            try{
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
                return buffer.toString();

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
            return null;
        }

//        @Override
//        protected void onPostExecute(Movie[] moviesArray) {
//
//            fillGrid(moviesArray);
//            updateDB(moviesArray);
//        }
    }
}
