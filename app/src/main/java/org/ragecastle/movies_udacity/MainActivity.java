package org.ragecastle.movies_udacity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.ragecastle.movies_udacity.adapters.Movie;
import org.ragecastle.movies_udacity.database.MoviesContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.main_container, new MainFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }

    }

    public void refresh() {
        FetchDataTask fetchMoviesTask = new FetchDataTask();
        fetchMoviesTask.execute(getSortBy());
    }

    private String getSortBy() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

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
                final String APIKEY = "";

                // Build the URI to pass in for movie information
                Uri builder = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, APIKEY)
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

            ContentValues movieValues;

            // Loop through static array of Flavors, add each to an instance of ContentValues
            // in the array of ContentValues
            for (int i = 0; i < moviesArray.length; i++) {
                movieValues = new ContentValues();
                movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, moviesArray[i].id);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, moviesArray[i].title);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_IMAGE, moviesArray[i].image);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, moviesArray[i].releaseDate);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_AVG_RATING, moviesArray[i].avgRating);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_PLOT, moviesArray[i].plot);

                // array to filter columns being queried
                String[] projection = {MoviesContract.MovieEntry.COLUMN_MOVIE_ID};
//
//                if (getContentResolver().query(
//                        MoviesContract.MovieEntry.CONTENT_URI,
//                        projection,
//                        null,
//                        null,
//                        null).moveToFirst()) {

                    // Check if the value is already in the database before adding it
                    if (getContentResolver().query(
                            MoviesContract.MovieEntry.CONTENT_URI.buildUpon()
                                    .appendPath(moviesArray[i].id)
                                    .build(),
                            projection,
                            null,
                            null,
                            null).getCount() == 0) {
                        Log.i(LOG_TAG, "Added " + moviesArray[i].title);
                        // add the movie to the database
                        getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieValues);
                    }
//                }
            }
        }
    }
}
