package org.ragecastle.movies_udacity.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.ragecastle.movies_udacity.R;

import java.util.List;

/**
 * Created by jahall on 11/1/15.
 *
 * ArrayAdapter to populate grid view of movie posters
 *
 */
public class MoviePosterAdapter extends ArrayAdapter<Movie> {
   // private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();
    private ImageView view;
    private final String BASE_URL = "http://image.tmdb.org/t/p/w185";

    public MoviePosterAdapter(Activity context, List<Movie> movie){
        super(context, 0, movie);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_poster, parent, false);
        }

        view = (ImageView) convertView.findViewById(R.id.list_item_poster_image);

        String url = BASE_URL.concat(movie.posterPath);

        Picasso.with(getContext()).load(url).fit().centerCrop().into(view);

        return convertView;
    }

    @Override
    public void add(Movie moviePoster) {
        String url = BASE_URL.concat(moviePoster.posterPath);
        Picasso.with(getContext()).load(url).fit().into(view);
    }

}
