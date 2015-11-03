package org.ragecastle.movies_udacity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jahall on 11/1/15.
 */
public class MoviePosterAdapter extends ArrayAdapter<MoviePoster> {
    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();
    private ImageView view;
    private int lastPosition;

    public MoviePosterAdapter(Activity context, List<MoviePoster> moviePosters){
        super(context, 0, moviePosters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        lastPosition = position;
        MoviePoster moviePoster = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_poster, parent, false);
        }

        view = (ImageView) convertView.findViewById(R.id.list_item_poster_image);

        String url = moviePoster.image;
        Picasso.with(getContext()).load(url).into(view);

        return convertView;
    }

    @Override
    public void add(MoviePoster moviePoster) {
        String url = moviePoster.image;
        Picasso.with(getContext()).load(url).into(view);
    }

}
