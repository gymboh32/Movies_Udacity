package org.ragecastle.movies_udacity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ragecastle.movies_udacity.adapters.Movie;

/**
 * Created by jahall on 11/1/15.
 *
 * Parse the movie data from the api call
 *
 */
public class MovieParser {

    public static Movie[] getMovieInfo(String movieData) throws JSONException {

        // String array of poster location to be returned
        Movie[] results;
        // Make the movieData parameter a JSONObject
        JSONObject jsonMovieData = new JSONObject(movieData);

        // Extract the list of results from movieData
        JSONArray movieInfoArray = jsonMovieData.getJSONArray("results");

        // Set the size of the results array based on the number of movies returned
        results = new Movie [movieInfoArray.length()];

        // Loop through the JSONArray and extract the poster location information
        for(int i=0;i<movieInfoArray.length();i++){

            // Pull the movieInfo from the Array
            // TODO: Pull the Trailer data
            // TODO: Pull the Reviews Data
            JSONObject movieInfo = movieInfoArray.getJSONObject(i);
            String id = getMovieData(movieInfo, "id");
            String title = getMovieData(movieInfo, "title");
            String posterPath = getMovieData(movieInfo, "poster_path");
            String releaseDate = getMovieData(movieInfo, "release_date");
            String avgRating = getMovieData(movieInfo, "vote_average");
            String plot = getMovieData(movieInfo, "overview");

            // Add the movie to the array
            // TODO: Put the Data into a Database instead of this
            results[i] = new Movie(id, title, posterPath, releaseDate, avgRating, plot);
        }

        // Return String array of poster locations
        return results;
    }

    public static String getMovieData(JSONObject movieInfo, String category) throws JSONException {
        // Return the String of movie data
        return movieInfo.getString(category);
    }
}
