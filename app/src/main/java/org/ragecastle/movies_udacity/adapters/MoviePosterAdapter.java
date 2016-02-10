package org.ragecastle.movies_udacity.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
//import android.widget.CursorAdapter;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.ragecastle.movies_udacity.R;
import org.ragecastle.movies_udacity.database.MoviesContract;
import org.ragecastle.movies_udacity.database.MoviesContract.DetailsEntry;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Created by jahall on 11/1/15.
 *
 * ArrayAdapter to populate grid view of movie posters
 *
 */
public class MoviePosterAdapter extends CursorAdapter {

    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    private final String BASE_URL = "http://image.tmdb.org/t/p/w185";

    public MoviePosterAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    private String getURL(Cursor cursor){

        return BASE_URL.concat(cursor.getString(
                cursor.getColumnIndex(
                        DetailsEntry.COLUMN_IMAGE)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context)
                .inflate(R.layout.list_item_poster, parent, false);
    }

    @Override
    public void bindView(View convertView, Context context, Cursor cursor) {
        ImageView view =
                (ImageView) convertView.findViewById(R.id.list_item_poster_image);

        // TODO: add error to display default image
        // TODO: Figure out this resize crap
        Picasso.with(context).load(getURL(cursor)).fit().centerCrop().into(view);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        if (oldCursor != null) {
            if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (newCursor != null) {
            if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
            // "Yeah, well, you know, that's just, like, your opinion, man."
            // - The Dude
            //
            // updated to look at new column
            mRowIDColumn = newCursor.getColumnIndexOrThrow(MoviesContract.SortEntry.COLUMN_MOVIE_ID);
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
        return oldCursor;
    }
}
