package edu.uw.databasedemo;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "Main";

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //controller
        AdapterView listView = (AdapterView)findViewById(R.id.wordListView);

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_layout, //item to inflate
                null, //cursor to show
                new String[] {WordDatabase.WordEntry.COL_WORD, WordDatabase.WordEntry.COL_COUNT}, //fields to display
                new int[] {R.id.txtItemWord, R.id.txtItemFreq},                           //where to display them
                0);
        listView.setAdapter(adapter);

        //load (read) the data
        getSupportLoaderManager().initLoader(0, null, this);

        //handle button input
        findViewById(R.id.btnAddWord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWord(v); //call helper method
            }
        });

        //handle item clicking
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item = (Cursor)parent.getItemAtPosition(position); //item we clicked on
                String word = item.getString(item.getColumnIndexOrThrow(WordDatabase.WordEntry.COL_WORD));
                int freq = item.getInt(item.getColumnIndexOrThrow(WordDatabase.WordEntry.COL_COUNT));
                Log.v(TAG, "Clicked on '"+word+"' ("+freq+")");

                setFrequency(id, freq+1);
            }
        });

    }

    //adds (creates) a word to the list
    public void addWord(View v){
        TextView inputText = (TextView)findViewById(R.id.txtAddWord);

        ContentValues newValues = new ContentValues();
        newValues.put(WordDatabase.WordEntry.COL_WORD, inputText.getText().toString());
        newValues.put(WordDatabase.WordEntry.COL_COUNT, 0);

        Uri newUri = this.getContentResolver().insert(
                WordProvider.CONTENT_URI,   // our provider!
                newValues                   // the values to insert
        );
        Log.v(TAG, "New word at: "+newUri);
    }

    //sets (updates) the frequency of the word with the given id
    public void setFrequency(long id, int newFrequency){
        ContentValues newValues = new ContentValues();
        newValues.put(WordDatabase.WordEntry.COL_COUNT, newFrequency);

        this.getContentResolver().update(
                ContentUris.withAppendedId(WordProvider.CONTENT_URI, id),
                newValues,
                null, null); //no selection
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //fields to fetch from the provider
        String[] projection = {WordDatabase.WordEntry._ID, WordDatabase.WordEntry.COL_WORD, WordDatabase.WordEntry.COL_COUNT};

        //create the CursorLoader to fetch data from the content provider
        CursorLoader loader = new CursorLoader(
                this,
                WordProvider.CONTENT_URI,
                projection,
                null, //no filter
                null,
                null //no sort
        );

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //replace the data
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //empty the data
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_test:
                WordDatabase.DatabaseHelper helper = new WordDatabase.DatabaseHelper(this);
                SQLiteDatabase db = helper.getReadableDatabase();

                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(WordDatabase.WordEntry.TABLE_NAME);

                Cursor results = builder.query(
                        db,
                        new String[] {WordDatabase.WordEntry.COL_WORD, WordDatabase.WordEntry.COL_COUNT},
                        null, null, null, null, null);

                while(results.moveToNext()){
                    String word = results.getString(results.getColumnIndexOrThrow(WordDatabase.WordEntry.COL_WORD));
                    int freq = results.getInt(results.getColumnIndexOrThrow(WordDatabase.WordEntry.COL_COUNT));
                    Log.v(TAG, "'"+word+"' ("+freq+")");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
