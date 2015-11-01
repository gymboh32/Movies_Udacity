package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jahall on 11/1/15.
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();
    // TODO: Change to pass in image
    private ArrayAdapter<String> posterAdapter;

    public MainFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create the rootView of the Fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create default data to be displayed
        // TODO: change to image view
        final String[] posterPathArray={
                "poster_1",
                "poster_2",
                "poster_3",
                "poster_4",
                "poster_5",
                "poster_6",
                "poster_7"
        };

        // Create list to be passed into array
        // TODO: Change to image path
        List<String> posterPath = new ArrayList<>(Arrays.asList(posterPathArray));

        // Create the adapter passing in the list
        // TODO: Change to image list instead of String
        posterAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_poster,
                R.id.list_item_poster_text,
                posterPath);

        // Create and populate grid view
        GridView gridView;
        gridView = (GridView) rootView.findViewById(R.id.gridview_posters);
        gridView.setAdapter(posterAdapter);

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            InputStream inputStream = null;
            StringBuffer buffer = null;
            String result = null;

            try{
                // constants of api parameters
                final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
                final String API_KEY_PARAM = "api_key";
                final String SORT_BY_PARAM = "sort_by";
                final String APIKEY = "b7a9ab2c1f215f3bb13a14d2dca30f56";

                // Build the URI to pass in for movie information
                Uri builder = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, APIKEY)
                        .appendQueryParameter(SORT_BY_PARAM, "")
                        .build();

                // Create URL to pass in for movie information
                URL url = new URL(builder.toString());

                // TODO: remove verbose log
                Log.v(LOG_TAG, url.toString());

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
                    buffer.append(line + "\n");
                }

                // Convert the buffer to String to be sent to the Parser
                result = buffer.toString();

            } catch (IOException e){
                Log.e(LOG_TAG, "Shit Broke");
            } finally {
                try {
                    reader.close();
                } catch (final IOException e){
                    Log.e(LOG_TAG, "Couldn't close reader");
                }
            }

            try {
                return MovieParser.getPoster(result);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] results) {
            if(results != null){
                posterAdapter.clear();
                for(String poster : results){
                    posterAdapter.add(poster);
                }
            }
        }
    }
}
