package com.example.harvestflow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}