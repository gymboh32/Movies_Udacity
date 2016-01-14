package org.ragecastle.movies_udacity.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by jahall on 11/30/15.
 *
 * Content Provider for movie data
 */

public class MoviesProvider extends ContentProvider {

    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MoviesDBHelper moviesDBHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int MOVIE_SORT_PARAM = 102;
    static final int DETAILS = 200;
    static final int DETAILS_WITH_ID = 201;

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_MOVIES + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MoviesContract.MovieEntry.TABLE_MOVIES + "/sort_by", MOVIE_SORT_PARAM);
        matcher.addURI(authority, MoviesContract.DetailsEntry.TABLE_DETAILS, DETAILS);
        matcher.addURI(authority, MoviesContract.DetailsEntry.TABLE_DETAILS + "/#", DETAILS_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        moviesDBHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;

        switch(uriMatcher.match(uri)){
            // All Movies selected
            case MOVIE:{
                retCursor = moviesDBHelper.getWritableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual movie based on Id selected
            case MOVIE_WITH_ID:{
                retCursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual movies based on sort parameter
            case MOVIE_SORT_PARAM:{
                retCursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_MOVIES,
                        projection,
                        MoviesContract.MovieEntry.COLUMN_SORT_PARAM + " = ?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case DETAILS:{
                retCursor = moviesDBHelper.getWritableDatabase().query(
                        MoviesContract.DetailsEntry.TABLE_DETAILS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual movie based on Id selected
            case DETAILS_WITH_ID:{
                retCursor = moviesDBHelper.getReadableDatabase().query(
                        MoviesContract.DetailsEntry.TABLE_DETAILS,
                        projection,
                        MoviesContract.DetailsEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match){
            case MOVIE:{
                return MoviesContract.MovieEntry.CONTENT_DIR_TYPE;
            }
            case MOVIE_WITH_ID:{
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            case MOVIE_SORT_PARAM:{
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_MOVIES, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MoviesContract.MovieEntry.buildMoviesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case DETAILS: {
                long _id = db.insert(MoviesContract.DetailsEntry.TABLE_DETAILS, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MoviesContract.DetailsEntry.buildMoviesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
            final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
            final int match = uriMatcher.match(uri);
            int numDeleted;
            switch(match){
                case MOVIE:
                    numDeleted = db.delete(
                            MoviesContract.MovieEntry.TABLE_MOVIES, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            MoviesContract.MovieEntry.TABLE_MOVIES + "'");
                    break;
                case MOVIE_WITH_ID:
                    numDeleted = db.delete(MoviesContract.MovieEntry.TABLE_MOVIES,
                            MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{String.valueOf(ContentUris.parseId(uri))});
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            MoviesContract.MovieEntry.TABLE_MOVIES + "'");
                    break;
                case MOVIE_SORT_PARAM:
                    numDeleted = db.delete(MoviesContract.MovieEntry.TABLE_MOVIES,
                            MoviesContract.MovieEntry.COLUMN_SORT_PARAM + " = ? ",
                            new String[]{String.valueOf(ContentUris.parseId(uri))});
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            MoviesContract.MovieEntry.TABLE_MOVIES + "'");
                    break;
                case DETAILS:
                    numDeleted = db.delete(
                            MoviesContract.DetailsEntry.TABLE_DETAILS, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            MoviesContract.DetailsEntry.TABLE_DETAILS + "'");
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            return numDeleted;
    }

    @Override
    public int update(Uri uri,
                      ContentValues values,
                      String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = moviesDBHelper.getWritableDatabase();
        int numUpdated = 0;

        if (values == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(uriMatcher.match(uri)){
            case MOVIE:{
                numUpdated = db.update(MoviesContract.MovieEntry.TABLE_MOVIES,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                numUpdated = db.update(MoviesContract.MovieEntry.TABLE_MOVIES,
                        values,
                        MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            case MOVIE_SORT_PARAM: {
                numUpdated = db.update(MoviesContract.MovieEntry.TABLE_MOVIES,
                        values,
                        MoviesContract.MovieEntry.COLUMN_SORT_PARAM + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}

