package com.example.harvestflow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectorDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "harvestflow.db";
    private static final int DATABASE_VERSION = 1;

    // Admin Table
    private static final String TABLE_ADMIN = "admin";
    private static final String ADMIN_USERNAME = "username";
    private static final String ADMIN_PASSWORD = "password";

    // Collectors Table
    private static final String TABLE_COLLECTORS = "collectors";
    private static final String COLLECTOR_ID = "id";
    private static final String COLLECTOR_NAME = "name";
    private static final String COLLECTOR_NIC = "nic";
    private static final String COLLECTOR_LOCATION = "location";
    private static final String COLLECTOR_AGE = "age";
    private static final String COLLECTOR_EMAIL = "email";
    private static final String COLLECTOR_PHONE = "phone";
    private static final String COLLECTOR_USERNAME = "username";
    private static final String COLLECTOR_PASSWORD = "password";

    public CollectorDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Admin Table
        db.execSQL("CREATE TABLE " + TABLE_ADMIN + " (" +
                ADMIN_USERNAME + " TEXT PRIMARY KEY, " +
                ADMIN_PASSWORD + " TEXT)");

        // Insert default admin
        db.execSQL("INSERT INTO " + TABLE_ADMIN + " (" + ADMIN_USERNAME + ", " + ADMIN_PASSWORD + ") VALUES ('admin', 'admin')");

        // Create Collectors Table
        db.execSQL("CREATE TABLE " + TABLE_COLLECTORS + " (" +
                COLLECTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLLECTOR_NAME + " TEXT, " +
                COLLECTOR_NIC + " TEXT, " +
                COLLECTOR_LOCATION + " TEXT, " +
                COLLECTOR_AGE + " INTEGER, " +
                COLLECTOR_EMAIL + " TEXT, " +
                COLLECTOR_PHONE + " TEXT, " +
                COLLECTOR_USERNAME + " TEXT UNIQUE, " +
                COLLECTOR_PASSWORD + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTORS);
        onCreate(db);
    }

    // Validate Admin Login
    public boolean validateAdmin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ADMIN +
                        " WHERE " + ADMIN_USERNAME + " = ? AND " + ADMIN_PASSWORD + " = ?",
                new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Add Collector
    public boolean addCollector(String name, String nic, String location, int age, String email, String phone, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLLECTOR_NAME, name);
        values.put(COLLECTOR_NIC, nic);
        values.put(COLLECTOR_LOCATION, location);
        values.put(COLLECTOR_AGE, age);
        values.put(COLLECTOR_EMAIL, email);
        values.put(COLLECTOR_PHONE, phone);
        values.put(COLLECTOR_USERNAME, username);
        values.put(COLLECTOR_PASSWORD, password);
        long result = db.insert(TABLE_COLLECTORS, null, values);
        return result != -1;
    }

    // Validate Collector Login
    public boolean validateCollector(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COLLECTORS +
                        " WHERE " + COLLECTOR_USERNAME + " = ? AND " + COLLECTOR_PASSWORD + " = ?",
                new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }
}

