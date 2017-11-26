package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private static final String LOG_TAG = CatalogActivity.class.getName();
    PetDbHelper mDbHelper = new PetDbHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        displayDatabaseInfo();
    }

    @Override
    public void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = { PetEntry._ID,
                                PetEntry.COLUMN_PET_NAME,
                                PetEntry.COLUMN_PET_BREED,
                                PetEntry.COLUMN_PET_GENDER,
                                PetEntry.COLUMN_PET_WEIGHT};

        Cursor cursor = db.query(PetEntry.TABLE_NAME,projection,null,null,null,null,null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount() + "\n");
            displayView.append("<ID>\tName\tBreed\tGender\tWeight\n");
            displayView.append("*****************************\n");

            int nameColIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int idColIndex = cursor.getColumnIndex(PetEntry._ID);
            int breedColIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
            Log.i(LOG_TAG,"name index: " + nameColIndex + "id index: " + idColIndex);
            while(cursor.moveToNext()){
                int thisId = cursor.getInt(idColIndex);
                String thisName = cursor.getString(nameColIndex);
                String thisBreed = cursor.getString(breedColIndex);
                int thisGender = cursor.getInt(genderColIndex);
                int thisWeight = cursor.getInt(weightColIndex);
                displayView.append("<" + thisId + ">\t" + thisName + "\t" + thisBreed + "\t" +
                        thisGender + "\t" + thisWeight + "\n");
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    private void insertData(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PetEntry.COLUMN_PET_NAME,"Toto");
        contentValues.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        contentValues.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        contentValues.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        long returnVal = db.insert(PetEntry.TABLE_NAME,null,contentValues);
        Log.i(LOG_TAG,"Return value is: " + returnVal);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}