package edu.uw.databasedemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WordProvider extends ContentProvider {
    // content://authority/resource/id

    private static final String AUTHORITY = "edu.uw.databasedemo.provider";
    private static final String WORD_RESOURCE = "words";
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+ WORD_RESOURCE);
    private static final int WORD_LIST_URI = 1;
    private static final int WORD_SINGLE_URI = 2;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH); // a matcher hooks things up together
    static {
        matcher.addURI(AUTHORITY, WORD_RESOURCE, WORD_LIST_URI); // asking for a list
        matcher.addURI(AUTHORITY, WORD_RESOURCE+"/#", WORD_SINGLE_URI); // looking for a specific word
    }

    public WordProvider() {
    }


    private WordDatabase.DatabaseHelper myHelper;

    @Override
    public boolean onCreate() {
        myHelper = new WordDatabase.DatabaseHelper(getContext());

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // when someone sends a query, lets get access to that database
        SQLiteDatabase db = myHelper.getWritableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(WordDatabase.Words.TABLE_NAME);

        switch (matcher.match(uri)) {
            case WORD_LIST_URI:

                break; // all we gotta do is give the table...
            case WORD_SINGLE_URI:
                // add a where clause-- only want to show them the single word that they requested
                builder.appendWhere(WordDatabase.Words._ID+"="+uri.getLastPathSegment());
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Cursor results = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        results.setNotificationUri(getContext().getContentResolver(), uri); // if you're wondering if anythings changed, here's where you go to look
        // ^^ registering a listener that if the db changes, re run the query.

        return results;
    }




    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {
            case WORD_LIST_URI:
                // return cursor type... a directory with a bunch of elements.  The cursory is on Authority.MyResource

                return "vnd.android.cursor.dir/" +AUTHORITY+"."+WORD_RESOURCE;

            case WORD_SINGLE_URI:
                return "vnd.anroid.cursor.item/"+AUTHORITY+"."+WORD_RESOURCE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
