package com.f20.favoriteplaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Using constants for column names
    public static final String DATABASE_NAME = "PlacesDatabase";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "place";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LNG = "longitude";
    public static final String COLUMN_CREATED = "date";
    public static final String COLUMN_VISITED = "visited";


    public DatabaseHelper(@Nullable Context context) {
        //cursor fatctory is when you are using you own custom cursor

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER NOT NULL CONSTRAINT employee_pk PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ADDRESS + " varchar(200) NOT NULL, " +
                COLUMN_CREATED + " varchar(200) NOT NULL, " +
                COLUMN_VISITED + " boolean DEFAULT 0, " +
                COLUMN_LAT + " double NOT NULL, " +
                COLUMN_LNG + " double NOT NULL);";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop the table and recreate it
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public boolean addPlace(Place place) {
        // In order to insert items into database, we need a writable database
        // this method returns a SQLite database instance
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        //Define contentValues instance
        ContentValues cv = new ContentValues();

        //The first argument of the put method is the column name and the second value
        cv.put(COLUMN_ADDRESS, place.getAddr());
        cv.put(COLUMN_CREATED, place.getDate());
        cv.put(COLUMN_VISITED, place.isVisited());
        cv.put(COLUMN_LAT,String.valueOf(place.getLat()));
        cv.put(COLUMN_LNG,String.valueOf(place.getLng()));

        // The insert method returns row number if the insertion is successful and -1 if it isn't
        return sqLiteDatabase.insert(TABLE_NAME, null, cv) != -1;

        //return true;
    }

    public Cursor getAllPlaces() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updatePlace(int id, Place place) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDRESS, place.getAddr());
        cv.put(COLUMN_CREATED, place.getDate());
        cv.put(COLUMN_VISITED, place.isVisited());
        cv.put(COLUMN_LAT,String.valueOf(place.getLat()));
        cv.put(COLUMN_LNG,String.valueOf(place.getLng()));

        //returns number of rows affected
        return sqLiteDatabase.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deletePlace(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        // delete method returns number of rows affected
        return sqLiteDatabase.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}
