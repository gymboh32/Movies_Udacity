package org.ragecastle.movies_udacity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jahall on 11/1/15.
 */
public class MovieParser {

    public static String[] getPoster(String movieData) throws JSONException {
        // String array of poster location to be returned
        String[] results;
        // Make the movieData parameter a JSONObject
        JSONObject jsonMovieData = new JSONObject(movieData);

        // Extract the list of results from movieData
        JSONArray movieInfoArray = jsonMovieData.getJSONArray("results");

        // Set the size of the results array based on the number of movies returned
        results = new String[movieInfoArray.length()];

        // Loop through the JSONArray and extract the poster location information
        for(int i=0;i<movieInfoArray.length();i++){

            // Pull the movieInfo from the Array
            JSONObject movieInfo = movieInfoArray.getJSONObject(i);
            // Pull the poster path info from the movieInfo
            String posterPath = movieInfo.getString("poster_path");
            // Add the poster path to the string array to be returned
            results[i] = posterPath;
        }

        // Return String array of poster locations
        return results;
    }
}
