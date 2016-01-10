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

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_AVG_RATING + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_PLOT + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);

        onCreate(db);
    }
}
