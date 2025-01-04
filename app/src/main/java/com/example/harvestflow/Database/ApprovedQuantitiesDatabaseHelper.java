package com.example.harvestflow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ApprovedQuantitiesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "harvest_flow.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    private static final String TABLE_QUANTITIES = "quantities";
    private static final String TABLE_FARMERS = "farmers";
    private static final String TABLE_RICE_TYPES = "rice_types";
    private static final String TABLE_MILL_QUANTITIES = "mill_quantities";

    // Common columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    // Quantities table columns
    private static final String FARMER_ID = "farmer_id";
    private static final String RICE_TYPE_ID = "rice_type_id";
    private static final String BAG_WEIGHT = "bag_weight";
    private static final String MOISTURE_WEIGHT = "moisture_weight";
    private static final String OTHER_DEDUCTIONS = "other_deductions";
    private static final String NET_WEIGHT = "net_weight";
    private static final String CREATED_AT = "created_at";
    private static final String STATUS = "status";

    // Mill quantities table columns
    private static final String MILL_ID = "mill_id";
    private static final String QUANTITY = "quantity";
    private static final String USERNAME = "username";

    public ApprovedQuantitiesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create farmers table
        String CREATE_FARMERS_TABLE = "CREATE TABLE " + TABLE_FARMERS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL" +
                ")";

        // Create rice_types table
        String CREATE_RICE_TYPES_TABLE = "CREATE TABLE " + TABLE_RICE_TYPES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL" +
                ")";

        // Create quantities table
        String CREATE_QUANTITIES_TABLE = "CREATE TABLE " + TABLE_QUANTITIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FARMER_ID + " INTEGER, " +
                RICE_TYPE_ID + " INTEGER, " +
                BAG_WEIGHT + " REAL, " +
                MOISTURE_WEIGHT + " REAL, " +
                OTHER_DEDUCTIONS + " REAL, " +
                NET_WEIGHT + " REAL, " +
                STATUS + " TEXT DEFAULT 'pending', " +
                CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + FARMER_ID + ") REFERENCES " + TABLE_FARMERS + "(id), " +
                "FOREIGN KEY(" + RICE_TYPE_ID + ") REFERENCES " + TABLE_RICE_TYPES + "(id)" +
                ")";

        // Create mill quantities table
        String CREATE_MILL_QUANTITIES_TABLE = "CREATE TABLE " + TABLE_MILL_QUANTITIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MILL_ID + " INTEGER NOT NULL, " +
                RICE_TYPE_ID + " INTEGER NOT NULL, " +
                QUANTITY + " REAL NOT NULL, " +
                USERNAME + " TEXT NOT NULL, " +
                STATUS + " TEXT DEFAULT 'pending', " +
                CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + RICE_TYPE_ID + ") REFERENCES " + TABLE_RICE_TYPES + "(id)" +
                ")";

        try {
            db.execSQL(CREATE_FARMERS_TABLE);
            db.execSQL(CREATE_RICE_TYPES_TABLE);
            db.execSQL(CREATE_QUANTITIES_TABLE);
            db.execSQL(CREATE_MILL_QUANTITIES_TABLE);
            insertSampleData(db);
        } catch (Exception e) {
            Log.e("Database", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MILL_QUANTITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FARMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RICE_TYPES);
        onCreate(db);
    }

    // Method to add quantity for mills
    public boolean addApprovedQuantity(int millId, int riceTypeId, double quantity, String username) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(MILL_ID, millId);
            values.put(RICE_TYPE_ID, riceTypeId);
            values.put(QUANTITY, quantity);
            values.put(USERNAME, username);
            values.put(STATUS, "pending");

            long result = db.insert(TABLE_MILL_QUANTITIES, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("Database", "Error adding approved quantity: " + e.getMessage());
            return false;
        }
    }

    // Get all mill quantities
    public Cursor getAllMillQuantities() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT mq.*, r.name as rice_type " +
                "FROM " + TABLE_MILL_QUANTITIES + " mq " +
                "JOIN " + TABLE_RICE_TYPES + " r ON mq." + RICE_TYPE_ID + " = r.id " +
                "ORDER BY mq." + CREATED_AT + " DESC";
        return db.rawQuery(query, null);
    }

    // Update mill quantity status
    public boolean updateMillQuantityStatus(int quantityId, String status) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(STATUS, status);

            int result = db.update(TABLE_MILL_QUANTITIES, values,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(quantityId)});
            return result > 0;
        } catch (Exception e) {
            Log.e("Database", "Error updating mill quantity status: " + e.getMessage());
            return false;
        }
    }

    // Original methods for farmer quantities
    public boolean addQuantity(int farmerId, int riceTypeId, double bagWeight,
                               double moistureWeight, double otherDeductions) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            double netWeight = bagWeight - (moistureWeight + otherDeductions);

            values.put(FARMER_ID, farmerId);
            values.put(RICE_TYPE_ID, riceTypeId);
            values.put(BAG_WEIGHT, bagWeight);
            values.put(MOISTURE_WEIGHT, moistureWeight);
            values.put(OTHER_DEDUCTIONS, otherDeductions);
            values.put(NET_WEIGHT, netWeight);
            values.put(STATUS, "pending");

            long result = db.insert(TABLE_QUANTITIES, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("Database", "Error adding quantity: " + e.getMessage());
            return false;
        }
    }

    public Cursor getAllQuantities() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT q.*, f.name as farmer_name, r.name as rice_type " +
                "FROM " + TABLE_QUANTITIES + " q " +
                "JOIN " + TABLE_FARMERS + " f ON q." + FARMER_ID + " = f.id " +
                "JOIN " + TABLE_RICE_TYPES + " r ON q." + RICE_TYPE_ID + " = r.id " +
                "ORDER BY q." + CREATED_AT + " DESC";
        return db.rawQuery(query, null);
    }

    public boolean updateQuantityStatus(int quantityId, String status) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(STATUS, status);

            int result = db.update(TABLE_QUANTITIES, values,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(quantityId)});
            return result > 0;
        } catch (Exception e) {
            Log.e("Database", "Error updating quantity status: " + e.getMessage());
            return false;
        }
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Insert sample farmers
        ContentValues farmerValues = new ContentValues();
        farmerValues.put(COLUMN_NAME, "John Doe");
        long farmer1Id = db.insert(TABLE_FARMERS, null, farmerValues);

        farmerValues.clear();
        farmerValues.put(COLUMN_NAME, "Jane Smith");
        long farmer2Id = db.insert(TABLE_FARMERS, null, farmerValues);

        // Insert sample rice types
        ContentValues riceTypeValues = new ContentValues();
        riceTypeValues.put(COLUMN_NAME, "Jasmine");
        long riceType1Id = db.insert(TABLE_RICE_TYPES, null, riceTypeValues);

        riceTypeValues.clear();
        riceTypeValues.put(COLUMN_NAME, "Basmati");
        long riceType2Id = db.insert(TABLE_RICE_TYPES, null, riceTypeValues);

        // Insert sample quantities
        addQuantityInternal(db, (int)farmer1Id, (int)riceType1Id, 100.0, 5.0, 2.0);
        addQuantityInternal(db, (int)farmer1Id, (int)riceType2Id, 150.0, 7.5, 3.0);
        addQuantityInternal(db, (int)farmer2Id, (int)riceType1Id, 120.0, 6.0, 2.5);
        addQuantityInternal(db, (int)farmer2Id, (int)riceType2Id, 180.0, 9.0, 4.0);
    }

    private void addQuantityInternal(SQLiteDatabase db, int farmerId, int riceTypeId,
                                     double bagWeight, double moistureWeight, double otherDeductions) {
        ContentValues values = new ContentValues();
        double netWeight = bagWeight - (moistureWeight + otherDeductions);

        values.put(FARMER_ID, farmerId);
        values.put(RICE_TYPE_ID, riceTypeId);
        values.put(BAG_WEIGHT, bagWeight);
        values.put(MOISTURE_WEIGHT, moistureWeight);
        values.put(OTHER_DEDUCTIONS, otherDeductions);
        values.put(NET_WEIGHT, netWeight);

        db.insert(TABLE_QUANTITIES, null, values);
    }
}