package org.ragecastle.movies_udacity.adapters;

/**
 * Created by jahall on 11/1/15.
 *
 *
 */
public class Movie {

    public String posterPath;
    public String id;
    public String title;
    public String releaseDate;
    public String avgRating;
    public String plot;

    public Movie( String id,
                  String title,
                  String posterPath,
                  String releaseDate,
                  String rating,
                  String overview)
    {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.avgRating = rating;
        this.plot = overview;
    }
}
