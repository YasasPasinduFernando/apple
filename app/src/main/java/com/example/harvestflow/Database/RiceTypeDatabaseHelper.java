package com.example.harvestflow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiceTypeDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "RiceTypeDatabaseHelper";
    private static final String DATABASE_NAME = "harvest_db.db";
    private static final int DATABASE_VERSION = 2;  // Match version with other helpers

    private static final String TABLE_RICE_TYPES = "rice_types";
    private static final String RICE_ID = "id";
    private static final String RICE_NAME = "name";
    private static final String RICE_TYPE = "type";
    private static final String PRICE_PER_KG = "price_per_kg";

    public RiceTypeDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createRiceTypesTable(db);
        insertSampleData(db);
    }

    private void createRiceTypesTable(SQLiteDatabase db) {
        try {
            String CREATE_RICE_TYPES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RICE_TYPES + "("
                    + RICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + RICE_NAME + " TEXT, "
                    + RICE_TYPE + " TEXT, "
                    + PRICE_PER_KG + " REAL)";
            db.execSQL(CREATE_RICE_TYPES_TABLE);
            Log.d(TAG, "Rice types table created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating rice types table: " + e.getMessage());
        }
    }

    private void insertSampleData(SQLiteDatabase db) {
        try {
            // Check if table is empty before inserting sample data
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RICE_TYPES, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            if (count == 0) {
                addRiceTypeInternal(db, "Jasmine", "Long Grain", 150.0);
                addRiceTypeInternal(db, "Basmati", "Premium", 200.0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inserting sample data: " + e.getMessage());
        }
    }

    private void addRiceTypeInternal(SQLiteDatabase db, String name, String type, double price) {
        ContentValues values = new ContentValues();
        values.put(RICE_NAME, name);
        values.put(RICE_TYPE, type);
        values.put(PRICE_PER_KG, price);
        db.insert(TABLE_RICE_TYPES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            // Only drop and recreate the rice_types table
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RICE_TYPES);
            createRiceTypesTable(db);
            insertSampleData(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
        }
    }

    public boolean addRiceType(String name, String type, double pricePerKg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RICE_NAME, name);
        values.put(RICE_TYPE, type);
        values.put(PRICE_PER_KG, pricePerKg);
        long result = db.insert(TABLE_RICE_TYPES, null, values);
        return result != -1;
    }

    public List<HashMap<String, String>> getAllRiceTypes() {
        List<HashMap<String, String>> riceTypes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RICE_TYPES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> riceType = new HashMap<>();
                riceType.put("id", cursor.getString(cursor.getColumnIndexOrThrow(RICE_ID)));
                riceType.put("name", cursor.getString(cursor.getColumnIndexOrThrow(RICE_NAME)));
                riceType.put("type", cursor.getString(cursor.getColumnIndexOrThrow(RICE_TYPE)));
                riceType.put("price_per_kg", String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(PRICE_PER_KG))));
                riceTypes.add(riceType);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return riceTypes;
    }

    public List<String> getAllRiceTypesFormatted() {
        List<String> riceTypes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RICE_TYPES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String riceInfo = cursor.getString(cursor.getColumnIndexOrThrow(RICE_NAME)) +
                        " (" + cursor.getString(cursor.getColumnIndexOrThrow(RICE_TYPE)) + ")";
                riceTypes.add(riceInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return riceTypes;
    }
}