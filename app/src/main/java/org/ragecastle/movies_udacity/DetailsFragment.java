package org.ragecastle.movies_udacity;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.ragecastle.movies_udacity.adapters.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jahall on 11/11/15.
 */
public class DetailsFragment extends Fragment {

    private final String LOG_TAG = DetailsFragment.class.getSimpleName();
    private TextView titleView;
    private TextView releaseDateView;
    private TextView voteAvgView;
    private TextView plotView;
    private ImageView poster;

    public DetailsFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            // Get movie id sent as extra text
            String movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            // Fetch the data for the movie
            FetchData fetchDataTask = new FetchData();
            fetchDataTask.execute(movieId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        titleView = (TextView) rootView.findViewById(R.id.movie_title_text);
        releaseDateView = (TextView) rootView.findViewById(R.id.movie_release_text);
        voteAvgView = (TextView) rootView.findViewById(R.id.movie_vote_text);
        plotView = (TextView) rootView.findViewById(R.id.movie_overview_text);
        poster = (ImageView) rootView.findViewById(R.id.poster_image);

        titleView.setText("title");
        releaseDateView.setText("release_date");
        voteAvgView.setText("average_vote");
        plotView.setText("overview");

        return rootView;
    }

    public class FetchData extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection;
            BufferedReader reader = null;
            InputStream inputStream;
            StringBuffer buffer;
            String result = null;

            try{
                // constants of api parameters
                final String BASE_URL = "https://api.themoviedb.org/3/movie";
                final String API_KEY_PARAM = "api_key";
                final String APIKEY = "";

                // Build the URI to pass in for movie information
                Uri builder = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
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

            return result;

//            try {
//                return result;
//            } catch (JSONException e){
//                Log.e(LOG_TAG, e.getMessage(), e);
//            }

//            return null;
        }

        @Override
        protected void onPostExecute(String results) {

            if (results != null) {
                try {
                    final String BASE_URL = "http://image.tmdb.org/t/p/w185";

                    // Get JSONObject to be parsed
                    JSONObject movieInfo = new JSONObject(results);

                    // Parse the movie info for specific data
                    String title = MovieParser.getMovieData(movieInfo, "original_title");
                    String releaseDate = MovieParser.getMovieData(movieInfo, "release_date");
                    String overview = MovieParser.getMovieData(movieInfo, "overview");
                    String avgVote = MovieParser.getMovieData(movieInfo, "vote_average");
                    String posterPath = MovieParser.getMovieData(movieInfo, "poster_path");
                    String url = BASE_URL.concat(posterPath);

                    // Set the new data to the views
                    titleView.setText(title);
                    releaseDateView.setText(releaseDate);
                    plotView.setText(overview);
                    voteAvgView.setText(avgVote);
                    Picasso.with(getActivity()).load(url).resize(370, 600).into(poster);

                }catch (JSONException e){
                    Log.d(LOG_TAG, "not converting");
                }
            }
        }
    }
}

