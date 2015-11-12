package org.ragecastle.movies_udacity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ragecastle.movies_udacity.adapters.Movie;

/**
 * Created by jahall on 11/1/15.
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
            JSONObject movieInfo = movieInfoArray.getJSONObject(i);
            String posterPath = getMovieData(movieInfo, "poster_path");
            String id = getMovieData(movieInfo, "id");

            // Add the poster path to the string array to be returned
            results[i] = new Movie(posterPath, id);
        }

        // Return String array of poster locations
        return results;
    }
//
//    public static String getPoster(JSONObject movieInfo) throws JSONException {
//
//        // Pull the poster path info from the movieInfo
//        String posterPath = movieInfo.getString("poster_path");
//
//        // Return String array of poster locations
//        return posterPath;
//    }
//
//    public static String getId(JSONObject movieInfo) throws JSONException {
//
//        // Pull the poster path info from the movieInfo
//        String movieId = movieInfo.getString("id");
//
//        // Return String array of poster locations
//        return movieId;
//    }

    public static String getMovieData(JSONObject movieInfo, String category) throws JSONException {

        // Pull the data from the movieInfo
        String movieData = movieInfo.getString(category);

        // Return the String of movie data
        return movieData;
    }
}
