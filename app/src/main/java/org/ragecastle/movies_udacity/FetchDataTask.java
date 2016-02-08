package org.ragecastle.movies_udacity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ragecastle.movies_udacity.database.MoviesContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jahall on 1/17/16.
 *
 * Async task to make api calls and put results in database
 *
 */
public class FetchDataTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchDataTask.class.getSimpleName();
    private final Context mContext;

    public FetchDataTask (Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Clear the database - DEBUGGING REASONS
        //////////////////////////////////////////////

//        mContext.getContentResolver().delete (
//                MoviesContract.DetailsEntry.CONTENT_URI,
//                null,
//                null);
//
        // TODO: check if in database
        Log.e(LOG_TAG, "removing all trailers");
        mContext.getContentResolver().delete(
                MoviesContract.TrailersEntry.CONTENT_URI,
                null,
                null);

        // TODO: check if in database
        Log.e(LOG_TAG, "removing all reviews");
        mContext.getContentResolver().delete (
                MoviesContract.ReviewsEntry.CONTENT_URI,
                null,
                null);
//
//        mContext.getContentResolver().delete(
//                MoviesContract.SortEntry.CONTENT_URI.buildUpon()
//                        .appendPath("sort_by")
//                        .build(),
//                null,
//                null);

        URL url = null;

        // constants of api parameters
        final String BASE_URL = "https://api.themoviedb.org/3";
        final String API_KEY_PARAM = "api_key";
        final String SORT_PARAM = "sort_by";
        final String APIKEY = "";
        final String POPULAR = "popularity.desc";
        final String RATING = "vote_average.desc";

        // Get Popular movies
        ///////////////////////

        // Build the URI to pass in for movie information
        Uri builder = Uri.parse(BASE_URL).buildUpon()
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter(API_KEY_PARAM, APIKEY)
                .appendQueryParameter(SORT_PARAM, POPULAR)
                .build();

        try{
            // Create URL to pass in for movie information
            url = new URL(builder.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, url.toString() + " failed to get popular movies");
        }

        String moviesByPopularity = getResults(url);

        try {
            putDetailsToDB(moviesByPopularity, POPULAR);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        // Get Rated movies
        /////////////////////

        // Build the URI to pass in for movie information
        builder = Uri.parse(BASE_URL).buildUpon()
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter(API_KEY_PARAM, APIKEY)
                .appendQueryParameter(SORT_PARAM, RATING)
                .build();

        try{
            // Create URL to pass in for movie information
            url = new URL(builder.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, url.toString() + " failed to get rated movies");
        }

        String moviesByRating = getResults(url);

        try {
            putDetailsToDB(moviesByRating, RATING);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        // Get List of Movies
        ///////////////////////

        Cursor cursor;
        String[] movieId;

        // get a list of movie ids
        String [] movieProjection = {MoviesContract.DetailsEntry.COLUMN_MOVIE_ID};

        cursor = mContext.getContentResolver().query(
                MoviesContract.DetailsEntry.CONTENT_URI,
                movieProjection,
                null,
                null,
                null);

        if (cursor == null) {
            // TODO: remove log
            Log.e(LOG_TAG, "No movies in DB");
        }

        movieId = new String[cursor.getCount()];

        if (cursor.moveToFirst()){
            do {
                movieId[cursor.getPosition()] =
                        cursor.getString(cursor.getColumnIndex(MoviesContract.DetailsEntry.COLUMN_MOVIE_ID));

            } while (cursor.moveToNext());
        }
        cursor.close();

        for (String aMovieId : movieId) {

            // Get Trailers
            /////////////////

            // Build the URI to pass in for movie information
            builder = Uri.parse(BASE_URL).buildUpon()
                    .appendPath("movie")
                    .appendPath(aMovieId)
                    .appendPath("videos")
                    .appendQueryParameter(API_KEY_PARAM, APIKEY)
                    .build();

            try {
                // Create URL to pass in for movie information
                url = new URL(builder.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, url.toString() + "failed to get trailers");
            }

            String moviesTrailers = getResults(url);

            try {
                putTrailersToDB(moviesTrailers, aMovieId);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            // Get Reviews
            ////////////////

            // Build the URI to pass in for movie information
            builder = Uri.parse(BASE_URL).buildUpon()
                    .appendPath("movie")
                    .appendPath(aMovieId)
                    .appendPath("reviews")
                    .appendQueryParameter(API_KEY_PARAM, APIKEY)
                    .build();

            try {
                // Create URL to pass in for movie information
                url = new URL(builder.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, url.toString() + "failed to get reviews");
            }

            String moviesReviews = getResults(url);

            try {
                putReviewsToDB(moviesReviews, aMovieId);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }
        return null;
    }

    private void putDetailsToDB(String apiResult, String sortBy) throws JSONException {

        ContentValues detailsValues;
        ContentValues sortValues;

        if(apiResult == null) {
            // TODO: Remove log
            Log.e(LOG_TAG, "I HATE this movie");
            return;
        }

        // Make the movieData parameter a JSONObject
        JSONObject jsonMovieData = new JSONObject(apiResult);

        // Extract the list of results from movieData
        JSONArray movieInfoArray = jsonMovieData.getJSONArray("results");

        // Loop through the JSONArray and extract the poster location information
        for (int i = 0; i < movieInfoArray.length(); i++) {

            // Pull the movieInfo from the Array
            JSONObject movieInfo = movieInfoArray.getJSONObject(i);

             // Check if movies are in database
            /////////////////////////////////////////
            Cursor cursor;
            String[] projection = {MoviesContract.DetailsEntry.COLUMN_MOVIE_ID};

            cursor = mContext.getContentResolver().query(
                    MoviesContract.DetailsEntry.CONTENT_URI.buildUpon()
                            .appendPath(movieInfo.getString("id"))
                            .build(),
                    projection,
                    null,
                    null,
                    null);

            Boolean isInDB = false;
            assert cursor != null;
            if (cursor.moveToFirst()) {
                do {
                    isInDB = cursor.getString(
                            cursor.getColumnIndex(
                                    MoviesContract.DetailsEntry.COLUMN_MOVIE_ID))
                            .contentEquals(movieInfo.getString("id")) || isInDB;
                } while (cursor.moveToNext());
            }
            cursor.close();

            if (!isInDB) {
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

                Log.i(LOG_TAG, "Adding " + movieInfo.getString("title") + " to DB");
                // add the movie to the database
                mContext.getContentResolver().insert(
                        MoviesContract.DetailsEntry.CONTENT_URI,
                        detailsValues);
            }

            // Check if values are in sort database
            /////////////////////////////////////////
            String[] sortProjection = {MoviesContract.SortEntry.COLUMN_MOVIE_ID,
                    MoviesContract.SortEntry.COLUMN_SORT_BY};

            cursor = mContext.getContentResolver().query(
                    MoviesContract.SortEntry.CONTENT_URI.buildUpon()
                            .appendPath(movieInfo.getString("id"))
                            .build(),
                    sortProjection,
                    null,
                    null,
                    null);

            Boolean isSorted = false;
            assert cursor != null;
            if (cursor.moveToFirst()) {
                do {
                    isSorted = cursor.getString(
                            cursor.getColumnIndex(MoviesContract.SortEntry.COLUMN_SORT_BY))
                            .contentEquals(sortBy) || isSorted;
                } while (cursor.moveToNext());
            }
            cursor.close();

            if (!isSorted) {
                sortValues = new ContentValues();
                sortValues.put(MoviesContract.SortEntry.COLUMN_SORT_BY, sortBy);
                sortValues.put(MoviesContract.SortEntry.COLUMN_MOVIE_ID, movieInfo.getString("id"));
//                sortValues.put(MoviesContract.SortEntry.COLUMN_IMAGE, movieInfo.getString("poster_path"));

                mContext.getContentResolver().insert(
                        MoviesContract.SortEntry.CONTENT_URI.buildUpon()
                                .appendPath("sort_by")
                                .build(),
                        sortValues);
            }
        }
    }

    private void putTrailersToDB(String apiResult, String movieId) throws JSONException {
        ContentValues trailerValues;

        if(apiResult==null) {
            // TODO: Remove log
            Log.e(LOG_TAG, "No trailers here");
            return;
        }

        // Make the movieData parameter a JSONObject
        JSONObject jsonMovieData = new JSONObject(apiResult);

        // Extract the list of results from movieData
        JSONArray movieInfoArray = jsonMovieData.getJSONArray("results");

        // Loop through the JSONArray and extract the poster location information
        for (int i = 0; i < movieInfoArray.length(); i++) {

             // Pull the movieInfo from the Array
            JSONObject movieInfo = movieInfoArray.getJSONObject(i);

            trailerValues = new ContentValues();
            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_MOVIE_ID,
                    movieId);
            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_TRAILER_ID,
                    movieInfo.getString("id"));
            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_TRAILER_KEY,
                    movieInfo.getString("key"));
            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_NAME,
                    movieInfo.getString("name"));
            trailerValues.put(MoviesContract.TrailersEntry.COLUMN_SITE,
                    movieInfo.getString("site"));

            // add the movie to the database
            mContext.getContentResolver().insert(MoviesContract.TrailersEntry.CONTENT_URI, trailerValues);

        }
    }

    private void putReviewsToDB(String apiResult, String movieId) throws JSONException {
        ContentValues reviewValues;

        if(apiResult == null) {
            // TODO: remove log
            Log.e(LOG_TAG, "No Review for you");
            return;
        }

            // Make the movieData parameter a JSONObject
            JSONObject jsonMovieData = new JSONObject(apiResult);

            // Extract the list of results from movieData
            JSONArray movieInfoArray = jsonMovieData.getJSONArray("results");


            // Loop through the JSONArray and extract the poster location information
            for (int i = 0; i < movieInfoArray.length(); i++) {

                // Pull the movieInfo from the Array
                JSONObject movieInfo = movieInfoArray.getJSONObject(i);

                reviewValues = new ContentValues();
                reviewValues.put(MoviesContract.TrailersEntry.COLUMN_MOVIE_ID,
                        movieId);
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID,
                        movieInfo.getString("id"));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_AUTHOR,
                        movieInfo.getString("author"));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_CONTENT,
                        movieInfo.getString("content"));
                reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_URL,
                        movieInfo.getString("url"));

                // add the movie to the database
                mContext.getContentResolver().insert(MoviesContract.ReviewsEntry.CONTENT_URI,
                        reviewValues);
            }
    }

    public String getResults(URL url){

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
            Log.e(LOG_TAG, "Check the API Key " + url.toString());
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

}
