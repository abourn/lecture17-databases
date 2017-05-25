package edu.uw.databasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by iguest on 5/25/17.
 */

public class WordDatabase {

    private WordDatabase() {} // do not instantiate

    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 1;

    public static class Words implements BaseColumns {
        public static final String TABLE_NAME = "words_table";
        public static final String COL_WORD = "db_word";
        public static final String COL_COUNT = "db_count";

    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String CREATE_WORD_TABLE = "CREATE TABLE " + Words.TABLE_NAME+ "(" +
                Words._ID +" INTEGER PRIMARY KEY AUTOINCREMENT" +
                "," + Words.COL_WORD + " Text" +
                "," + Words.COL_COUNT + " INTEGER" + ")";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_WORD_TABLE);
            ContentValues bundle = new ContentValues();
            bundle.put(Words.COL_COUNT, 0);
            bundle.put(Words.COL_WORD, "cromulent");

            db.insert(Words.TABLE_NAME, null, bundle);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE " + Words.TABLE_NAME+ " IF EXISTS"); // drops table, so we'll lose the data, but we don't care for purposes of demo
            onCreate(db);
        }
    }

}
