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

public class RiceMillDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "RiceMillDatabaseHelper";
    private static final String DATABASE_NAME = "harvest_db.db";
    private static final int DATABASE_VERSION = 2;

    // Table and column names
    private static final String TABLE_RICE_MILLS = "rice_mills";
    private static final String MILL_ID = "id";
    private static final String MILL_NAME = "name";
    private static final String MILL_LOCATION = "location";
    private static final String OWNER_CONTACT = "contact_number";
    private static final String BUSINESS_REG_ID = "business_reg_id";

    public RiceMillDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_RICE_MILLS_TABLE = "CREATE TABLE " + TABLE_RICE_MILLS + "("
                    + MILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MILL_NAME + " TEXT NOT NULL, "
                    + MILL_LOCATION + " TEXT NOT NULL, "
                    + OWNER_CONTACT + " TEXT NOT NULL, "
                    + BUSINESS_REG_ID + " TEXT NOT NULL UNIQUE)";
            db.execSQL(CREATE_RICE_MILLS_TABLE);
            Log.d(TAG, "Database tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RICE_MILLS);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
        }
    }

    // Check if a business registration ID already exists
    public boolean isBusinessRegIdExists(String regId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RICE_MILLS,
                    new String[]{BUSINESS_REG_ID},
                    BUSINESS_REG_ID + "=?",
                    new String[]{regId},
                    null, null, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking business registration ID: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Add a new rice mill
    public boolean addRiceMill(String name, String location, String contact, String regId) {
        if (isBusinessRegIdExists(regId)) {
            Log.d(TAG, "Business registration ID already exists: " + regId);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(MILL_NAME, name);
            values.put(MILL_LOCATION, location);
            values.put(OWNER_CONTACT, contact);
            values.put(BUSINESS_REG_ID, regId);

            long result = db.insert(TABLE_RICE_MILLS, null, values);
            if (result != -1) {
                Log.d(TAG, "Successfully added rice mill: " + name);
                return true;
            } else {
                Log.e(TAG, "Failed to add rice mill: " + name);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding rice mill: " + e.getMessage());
            return false;
        }
    }

    // Get all rice mills as a list of HashMaps
    public List<HashMap<String, String>> getAllRiceMills() {
        List<HashMap<String, String>> riceMills = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_RICE_MILLS, null, null, null, null, null,
                    MILL_NAME + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> riceMill = new HashMap<>();
                    riceMill.put("id", cursor.getString(cursor.getColumnIndexOrThrow(MILL_ID)));
                    riceMill.put("name", cursor.getString(cursor.getColumnIndexOrThrow(MILL_NAME)));
                    riceMill.put("location", cursor.getString(cursor.getColumnIndexOrThrow(MILL_LOCATION)));
                    riceMill.put("contact", cursor.getString(cursor.getColumnIndexOrThrow(OWNER_CONTACT)));
                    riceMill.put("regId", cursor.getString(cursor.getColumnIndexOrThrow(BUSINESS_REG_ID)));
                    riceMills.add(riceMill);
                } while (cursor.moveToNext());
            }
            return riceMills;
        } catch (Exception e) {
            Log.e(TAG, "Error getting all rice mills: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Get all rice mills formatted as strings
    public List<String> getAllRiceMillsFormatted() {
        List<String> riceMills = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_RICE_MILLS, null, null, null, null, null,
                    MILL_NAME + " ASC");

            if (cursor.moveToFirst()) {
                do {
                    String millInfo = cursor.getString(cursor.getColumnIndexOrThrow(MILL_NAME)) +
                            " - " + cursor.getString(cursor.getColumnIndexOrThrow(MILL_LOCATION)) +
                            " (Reg: " + cursor.getString(cursor.getColumnIndexOrThrow(BUSINESS_REG_ID)) + ")";
                    riceMills.add(millInfo);
                } while (cursor.moveToNext());
            }
            return riceMills;
        } catch (Exception e) {
            Log.e(TAG, "Error getting formatted rice mills: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Get a single rice mill by ID
    public HashMap<String, String> getRiceMillById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, String> riceMill = new HashMap<>();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_RICE_MILLS, null,
                    MILL_ID + "=?", new String[]{id},
                    null, null, null);

            if (cursor.moveToFirst()) {
                riceMill.put("id", cursor.getString(cursor.getColumnIndexOrThrow(MILL_ID)));
                riceMill.put("name", cursor.getString(cursor.getColumnIndexOrThrow(MILL_NAME)));
                riceMill.put("location", cursor.getString(cursor.getColumnIndexOrThrow(MILL_LOCATION)));
                riceMill.put("contact", cursor.getString(cursor.getColumnIndexOrThrow(OWNER_CONTACT)));
                riceMill.put("regId", cursor.getString(cursor.getColumnIndexOrThrow(BUSINESS_REG_ID)));
            }
            return riceMill;
        } catch (Exception e) {
            Log.e(TAG, "Error getting rice mill by ID: " + e.getMessage());
            return new HashMap<>();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Update rice mill details
    public boolean updateRiceMill(String id, String name, String location, String contact, String regId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            // Check if new regId already exists for a different mill
            Cursor cursor = db.query(TABLE_RICE_MILLS, new String[]{MILL_ID},
                    BUSINESS_REG_ID + "=? AND " + MILL_ID + "!=?",
                    new String[]{regId, id}, null, null, null);

            if (cursor.getCount() > 0) {
                cursor.close();
                Log.d(TAG, "Business registration ID already exists for different mill: " + regId);
                return false;
            }
            cursor.close();

            values.put(MILL_NAME, name);
            values.put(MILL_LOCATION, location);
            values.put(OWNER_CONTACT, contact);
            values.put(BUSINESS_REG_ID, regId);

            int result = db.update(TABLE_RICE_MILLS, values,
                    MILL_ID + "=?", new String[]{id});

            if (result > 0) {
                Log.d(TAG, "Successfully updated rice mill with ID: " + id);
                return true;
            } else {
                Log.d(TAG, "No rice mill found with ID: " + id);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating rice mill: " + e.getMessage());
            return false;
        }
    }

    // Delete a rice mill
    public boolean deleteRiceMill(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int result = db.delete(TABLE_RICE_MILLS,
                    MILL_ID + "=?", new String[]{id});

            if (result > 0) {
                Log.d(TAG, "Successfully deleted rice mill with ID: " + id);
                return true;
            } else {
                Log.d(TAG, "No rice mill found with ID: " + id);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting rice mill: " + e.getMessage());
            return false;
        }
    }

    // Get total count of rice mills
    public int getRiceMillCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RICE_MILLS, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting rice mill count: " + e.getMessage());
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}