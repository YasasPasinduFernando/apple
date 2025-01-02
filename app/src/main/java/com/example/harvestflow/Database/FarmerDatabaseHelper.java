package com.example.harvestflow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FarmerDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "farmer_db.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_FARMERS = "farmers";
    private static final String FARMER_ID = "id";
    private static final String FARMER_NAME = "name";
    private static final String FARMER_NIC = "nic";
    private static final String FARMER_LOCATION = "location";
    private static final String FARMER_AGE = "age";
    private static final String FARMER_EMAIL = "email";
    private static final String FARMER_PHONE = "phone";
    private static final String FARMER_COLLECTOR_ID = "collector_id";
    private static final String FARMER_LAND_SIZE = "land_size";

    public FarmerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FARMERS_TABLE = "CREATE TABLE " + TABLE_FARMERS + "("
                + FARMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FARMER_NAME + " TEXT, "
                + FARMER_NIC + " TEXT, "
                + FARMER_LOCATION + " TEXT, "
                + FARMER_AGE + " INTEGER, "
                + FARMER_EMAIL + " TEXT, "
                + FARMER_PHONE + " TEXT, "
                + FARMER_COLLECTOR_ID + " INTEGER, "
                + FARMER_LAND_SIZE + " REAL)";
        db.execSQL(CREATE_FARMERS_TABLE);

        // Insert initial data
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        addFarmerInternal(db, "John Doe", "123456789V", "Colombo", 45,
                "john@example.com", "0771234567", 1, 5.5);
        addFarmerInternal(db, "Jane Smith", "987654321V", "Kandy", 38,
                "jane@example.com", "0777654321", 1, 3.2);
    }

    private void addFarmerInternal(SQLiteDatabase db, String name, String nic,
                                   String location, int age, String email,
                                   String phone, int collectorId, double landSize) {
        ContentValues values = new ContentValues();
        values.put(FARMER_NAME, name);
        values.put(FARMER_NIC, nic);
        values.put(FARMER_LOCATION, location);
        values.put(FARMER_AGE, age);
        values.put(FARMER_EMAIL, email);
        values.put(FARMER_PHONE, phone);
        values.put(FARMER_COLLECTOR_ID, collectorId);
        values.put(FARMER_LAND_SIZE, landSize);
        db.insert(TABLE_FARMERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FARMERS);
        onCreate(db);
    }

    public boolean addFarmer(String name, String nic, String location, int age,
                             String email, String phone, int collectorId, double landSize) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FARMER_NAME, name);
        values.put(FARMER_NIC, nic);
        values.put(FARMER_LOCATION, location);
        values.put(FARMER_AGE, age);
        values.put(FARMER_EMAIL, email);
        values.put(FARMER_PHONE, phone);
        values.put(FARMER_COLLECTOR_ID, collectorId);
        values.put(FARMER_LAND_SIZE, landSize);

        long result = db.insert(TABLE_FARMERS, null, values);
        return result != -1;
    }

    public List<HashMap<String, String>> getAllFarmers() {
        List<HashMap<String, String>> farmerList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FARMERS, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> farmer = new HashMap<>();
                farmer.put("id", cursor.getString(cursor.getColumnIndexOrThrow(FARMER_ID)));
                farmer.put("name", cursor.getString(cursor.getColumnIndexOrThrow(FARMER_NAME)));
                farmer.put("location", cursor.getString(cursor.getColumnIndexOrThrow(FARMER_LOCATION)));
                farmerList.add(farmer);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return farmerList;
    }

    public List<String> getAllFarmersFormatted() {
        List<String> farmers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FARMERS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String farmerInfo = cursor.getString(cursor.getColumnIndexOrThrow(FARMER_NAME)) +
                        " (" + cursor.getString(cursor.getColumnIndexOrThrow(FARMER_LOCATION)) + ")";
                farmers.add(farmerInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return farmers;
    }
}