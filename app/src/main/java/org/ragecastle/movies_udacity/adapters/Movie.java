package org.ragecastle.movies_udacity.adapters;

/**
 * Created by jahall on 11/1/15.
 *
 *
 */
public class Movie {

    public String id;
    public String title;
    public String image;
    public String releaseDate;
    public String avgRating;
    public String plot;
    public String trailer;
    public String reviews;


    public Movie( String id,
                  String title,
                  String image,
                  String releaseDate,
                  String rating,
                  String plot,
                  String trailer,
                  String reviews)
    {
        this.id = id;
        this.title = title;
        this.image = image;
        this.releaseDate = releaseDate;
        this.avgRating = rating;
        this.plot = plot;
        this.trailer = trailer;
        this.reviews = reviews;
    }
}
