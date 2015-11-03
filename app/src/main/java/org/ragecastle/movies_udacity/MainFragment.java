package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.SharedPreferences;
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
import android.widget.GridView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by jahall on 11/1/15.
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();
    // TODO: Change to pass in image
//    private ArrayAdapter<String> posterAdapter;
    private MoviePosterAdapter posterAdapter;
    private View rootView;
    private GridView gridView;

    public MainFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refresh();
//        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
//        fetchMoviesTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // We has menu options
        setHasOptionsMenu(true);

        // Create the rootView of the Fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        MoviePoster[] posterPathArray = {
                new MoviePoster("http://image.tmdb.org/t/p/w500/t90Y3G8UGQp0f0DrP60wRu9gfrH.jpg")
        };

        gridView = (GridView) rootView.findViewById(R.id.gridview_posters);

        fillGrid(posterPathArray);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();

        switch (id) {
            case R.id.action_refresh:
                // TODO: Remove verbose logs
                Log.v(LOG_TAG, "attempted to refresh screen");
                fetchMoviesTask.execute(getSortBy());
                return true;
            case R.id.action_sort_popular:
                // TODO: Remove verbose logs
                Log.v(LOG_TAG, "Sort By: Popularity");
                fetchMoviesTask.execute("popularity.desc");
                return true;
            case R.id.action_sort_rating:
                // TODO: Remove verbose logs
                Log.v(LOG_TAG, "Sort By: Rating");
                fetchMoviesTask.execute("vote_average.desc");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fillGrid(MoviePoster[] moviePosters){
        posterAdapter = new MoviePosterAdapter(getActivity(), Arrays.asList(moviePosters));
        // Populate grid view
        gridView.setAdapter(posterAdapter);

    }

    private void refresh() {
        // TODO: Remove verbose logs
        Log.v(LOG_TAG, "attempted to refresh screen");
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(getSortBy());
    }

    public String getSortBy() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPref.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_default_sort));
        // TODO: remove verbose log
        Log.v(LOG_TAG, sortBy);

        return sortBy;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]>{

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

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
                final String APIKEY = "b7a9ab2c1f215f3bb13a14d2dca30f56";

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

            final String BASE_URL = "http://image.tmdb.org/t/p/w780";
            if(results != null){

                MoviePoster[] moviePosters = new MoviePoster[results.length];
                for(int i=0;i<results.length;i++){
                    moviePosters[i] = new MoviePoster(BASE_URL.concat(results[i]));
                    posterAdapter.add(moviePosters[i]);
                }

                fillGrid(moviePosters);
            }
        }

    }
}
