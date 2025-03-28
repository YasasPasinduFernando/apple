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

public class ApprovedQuantitiesDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "ApprovedQuantitiesDB";
    private static final String DATABASE_NAME = "approved_quantities.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_APPROVED_QUANTITIES = "approved_quantities";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MILL_ID = "mill_id";
    private static final String COLUMN_RICE_TYPE_ID = "rice_type_id";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private final Context context;
    private final RiceMillDatabaseHelper riceMillDbHelper;
    private final RiceTypeDatabaseHelper riceTypeDbHelper;

    public ApprovedQuantitiesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.riceMillDbHelper = new RiceMillDatabaseHelper(context);
        this.riceTypeDbHelper = new RiceTypeDatabaseHelper(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_APPROVED_QUANTITIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MILL_ID + " INTEGER NOT NULL, "
                + COLUMN_RICE_TYPE_ID + " INTEGER NOT NULL, "
                + COLUMN_QUANTITY + " REAL NOT NULL, "
                + COLUMN_USERNAME + " TEXT NOT NULL, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

        try {
            db.execSQL(CREATE_TABLE);
            Log.d(TAG, "Approved quantities table created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating approved quantities table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPROVED_QUANTITIES);
            onCreate(db);
        }
    }

    /**
     * Adds an approved quantity to the database.
     *
     * @param millId    The ID of the mill.
     * @param riceTypeId The ID of the rice type.
     * @param quantity  The approved quantity.
     * @param username  The username who approved it.
     * @return true if insertion is successful, false otherwise.
     */
    public boolean addApprovedQuantity(int millId, int riceTypeId, double quantity, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_MILL_ID, millId);
            values.put(COLUMN_RICE_TYPE_ID, riceTypeId);
            values.put(COLUMN_QUANTITY, quantity);
            values.put(COLUMN_USERNAME, username);

            long result = db.insert(TABLE_APPROVED_QUANTITIES, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding approved quantity: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all approved quantities with mill names and rice types.
     */
    public List<HashMap<String, String>> getAllApprovedQuantities() {
        List<HashMap<String, String>> approvedQuantities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get the base approved quantities data
        String query = "SELECT * FROM " + TABLE_APPROVED_QUANTITIES +
                " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> quantity = new HashMap<>();
                    int millId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MILL_ID));
                    int riceTypeId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RICE_TYPE_ID));

                    // Get mill name from RiceMillDatabaseHelper
                    String millName = getMillName(millId);

                    // Get rice type from RiceTypeDatabaseHelper
                    String riceType = getRiceTypeName(riceTypeId);

                    quantity.put("id", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    quantity.put("mill_name", millName);
                    quantity.put("rice_type", riceType);
                    quantity.put("quantity", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)));
                    quantity.put("username", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                    quantity.put("timestamp", cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));

                    approvedQuantities.add(quantity);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting approved quantities: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return approvedQuantities;
    }

    /**
     * Helper method to get mill name from RiceMillDatabaseHelper
     */
    private String getMillName(int millId) {
        List<HashMap<String, String>> mills = riceMillDbHelper.getAllRiceMills();
        for (HashMap<String, String> mill : mills) {
            if (mill.get("id") != null && Integer.parseInt(mill.get("id")) == millId) {
                return mill.get("name");
            }
        }
        return "Unknown Mill";
    }

    /**
     * Helper method to get rice type name from RiceTypeDatabaseHelper
     */
    private String getRiceTypeName(int riceTypeId) {
        List<HashMap<String, String>> riceTypes = riceTypeDbHelper.getAllRiceTypes();
        for (HashMap<String, String> riceType : riceTypes) {
            if (riceType.get("id") != null && Integer.parseInt(riceType.get("id")) == riceTypeId) {
                return riceType.get("name");
            }
        }
        return "Unknown Rice Type";
    }
    public double getTotalQuantity() {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;

        String query = "SELECT SUM(" + COLUMN_QUANTITY + ") as total FROM " + TABLE_APPROVED_QUANTITIES;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total quantity: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }



    /**
     * Retrieves the total approved quantity for a specific mill.
     *
     * @param millId The ID of the mill.
     * @return The total approved quantity.
     */
    public double getTotalQuantityForMill(int millId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;

        String query = "SELECT SUM(" + COLUMN_QUANTITY + ") as total FROM " + TABLE_APPROVED_QUANTITIES +
                " WHERE " + COLUMN_MILL_ID + "=?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(millId)});
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting total quantity: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

}
