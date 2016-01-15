package org.ragecastle.movies_udacity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jahall on 11/30/15.
 *
 * Database helper
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 5;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_SORT_PARAM + " TEXT NOT NULL, " +
                " UNIQUE (" + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_DETAILS_TABLE = "CREATE TABLE " +
                MoviesContract.DetailsEntry.TABLE_DETAILS + " (" +
                MoviesContract.DetailsEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesContract.DetailsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.DetailsEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                MoviesContract.DetailsEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.DetailsEntry.COLUMN_AVG_RATING + " TEXT NOT NULL, " +
                MoviesContract.DetailsEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.DetailsEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_MOVIES + " (" + MoviesContract.MovieEntry.COLUMN_MOVIE_ID +
                "), UNIQUE (" + MoviesContract.DetailsEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                MoviesContract.TrailersEntry.TABLE_TRAILERS + " (" +
                MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.COLUMN_SITE + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.TrailersEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_MOVIES + " (" + MoviesContract.MovieEntry.COLUMN_MOVIE_ID +
                "));";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_DETAILS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.DetailsEntry.TABLE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailersEntry.TABLE_TRAILERS);

        onCreate(db);
    }
}
