package org.ragecastle.movies_udacity;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.ragecastle.movies_udacity.adapters.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jahall on 11/30/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    @Override
    protected Movie[] doInBackground(String... params) {

        // TODO: Fetch Trailers
        // TODO: Fetch Reviews
        HttpURLConnection connection;
        BufferedReader reader = null;
        InputStream inputStream;
        StringBuffer buffer;
        String result = null;

        try{
            // constants of api parameters
            final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
            final String API_KEY_PARAM = "api_key";
            final String SORT_BY_PARAM = "sort_by";
            // TODO: Remove APIKEY
            final String APIKEY = "";

            // Build the URI to pass in for movie information
            Uri builder = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, APIKEY)
                    .appendQueryParameter(SORT_BY_PARAM, params[0])
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
            if(inputStream == null){
                return null;
            }

            // Read the input stream
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Write the reader to the buffer as long as there is something to write
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line).append("\n");
            }

            // Convert the buffer to String to be sent to the Parser
            result = buffer.toString();

        } catch (IOException e){
            Log.e(LOG_TAG, "Check the API Key");
        } finally {
            try {
                if(reader != null) {reader.close();}
            } catch (final IOException e){
                Log.e(LOG_TAG, "Couldn't close reader");
            }
        }

        try {
            return MovieParser.getMovieInfo(result);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie[] results) {

        // TODO: Fix this
        if(results != null){
            fillGrid(results);
        }
    }

}
