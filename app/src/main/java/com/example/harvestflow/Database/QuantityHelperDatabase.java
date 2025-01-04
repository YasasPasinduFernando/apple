package com.example.harvestflow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuantityHelperDatabase extends SQLiteOpenHelper {
    private static final String TAG = "QuantityHelperDatabase";
    private static final String DATABASE_NAME = "harvest_flow.db";
    private static final int DATABASE_VERSION = 2;

    // Tables
    private static final String TABLE_QUANTITIES = "quantities";
    private static final String TABLE_FARMERS = "farmers";
    private static final String TABLE_RICE_TYPES = "rice_types";

    // Common columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    // Quantities table columns
    private static final String FARMER_ID = "farmer_id";
    private static final String RICE_TYPE_ID = "rice_type_id";
    private static final String TOTAL_WEIGHT = "total_weight";
    private static final String BAG_WEIGHT = "bag_weight";
    private static final String MOISTURE_WEIGHT = "moisture_weight";
    private static final String OTHER_DEDUCTIONS = "other_deductions";
    private static final String NET_WEIGHT = "net_weight";
    private static final String CREATED_AT = "created_at";

    public QuantityHelperDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // Create farmers table
            String CREATE_FARMERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FARMERS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL UNIQUE" +
                    ")";

            // Create rice_types table
            String CREATE_RICE_TYPES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RICE_TYPES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL UNIQUE" +
                    ")";

            // Create quantities table
            String CREATE_QUANTITIES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_QUANTITIES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FARMER_ID + " INTEGER NOT NULL, " +
                    RICE_TYPE_ID + " INTEGER NOT NULL, " +
                    TOTAL_WEIGHT + " REAL NOT NULL CHECK(" + TOTAL_WEIGHT + " > 0), " +
                    BAG_WEIGHT + " REAL NOT NULL CHECK(" + BAG_WEIGHT + " >= 0), " +
                    MOISTURE_WEIGHT + " REAL NOT NULL CHECK(" + MOISTURE_WEIGHT + " >= 0), " +
                    OTHER_DEDUCTIONS + " REAL NOT NULL CHECK(" + OTHER_DEDUCTIONS + " >= 0), " +
                    NET_WEIGHT + " REAL NOT NULL CHECK(" + NET_WEIGHT + " >= 0), " +
                    CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(" + FARMER_ID + ") REFERENCES " + TABLE_FARMERS + "(" + COLUMN_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + RICE_TYPE_ID + ") REFERENCES " + TABLE_RICE_TYPES + "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ")";

            db.execSQL(CREATE_FARMERS_TABLE);
            db.execSQL(CREATE_RICE_TYPES_TABLE);
            db.execSQL(CREATE_QUANTITIES_TABLE);

            insertSampleData(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error creating tables: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void insertSampleData(SQLiteDatabase db) {
        try {
            // Insert sample farmers
            ContentValues farmerValues = new ContentValues();
            String[] farmers = {"John Doe", "Jane Smith"};
            long[] farmerIds = new long[2];

            for (int i = 0; i < farmers.length; i++) {
                farmerValues.clear();
                farmerValues.put(COLUMN_NAME, farmers[i]);
                farmerIds[i] = db.insertWithOnConflict(TABLE_FARMERS, null, farmerValues,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }

            // Insert sample rice types
            ContentValues riceTypeValues = new ContentValues();
            String[] riceTypes = {"Jasmine", "Basmati"};
            long[] riceTypeIds = new long[2];

            for (int i = 0; i < riceTypes.length; i++) {
                riceTypeValues.clear();
                riceTypeValues.put(COLUMN_NAME, riceTypes[i]);
                riceTypeIds[i] = db.insertWithOnConflict(TABLE_RICE_TYPES, null, riceTypeValues,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }

            // Insert sample quantities only if both farmers and rice types were inserted
            if (farmerIds[0] > 0 && riceTypeIds[0] > 0) {
                double[][] sampleData = {
                        {120.0, 100.0, 5.0, 2.0},
                        {170.0, 150.0, 7.5, 3.0},
                        {140.0, 120.0, 6.0, 2.5},
                        {200.0, 180.0, 9.0, 4.0}
                };

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        double[] data = sampleData[i * 2 + j];
                        addQuantityInternal(db, (int)farmerIds[i], (int)riceTypeIds[j],
                                data[0], data[1], data[2], data[3]);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inserting sample data: " + e.getMessage());
        }
    }

    private void addQuantityInternal(SQLiteDatabase db, int farmerId, int riceTypeId,
                                     double totalWeight, double bagWeight, double moistureWeight,
                                     double otherDeductions) {
        try {
            ContentValues values = new ContentValues();
            double netWeight = calculateNetWeight(totalWeight, bagWeight, moistureWeight, otherDeductions);

            values.put(FARMER_ID, farmerId);
            values.put(RICE_TYPE_ID, riceTypeId);
            values.put(TOTAL_WEIGHT, totalWeight);
            values.put(BAG_WEIGHT, bagWeight);
            values.put(MOISTURE_WEIGHT, moistureWeight);
            values.put(OTHER_DEDUCTIONS, otherDeductions);
            values.put(NET_WEIGHT, netWeight);

            db.insertOrThrow(TABLE_QUANTITIES, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding quantity: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            // Backup existing data if needed
            // For this version, we'll just recreate the tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FARMERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RICE_TYPES);
            onCreate(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    public boolean addQuantity(int farmerId, int riceTypeId, double totalWeight,
                               double bagWeight, double moistureWeight, double otherDeductions) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            double netWeight = calculateNetWeight(totalWeight, bagWeight, moistureWeight, otherDeductions);

            values.put(FARMER_ID, farmerId);
            values.put(RICE_TYPE_ID, riceTypeId);
            values.put(TOTAL_WEIGHT, totalWeight);
            values.put(BAG_WEIGHT, bagWeight);
            values.put(MOISTURE_WEIGHT, moistureWeight);
            values.put(OTHER_DEDUCTIONS, otherDeductions);
            values.put(NET_WEIGHT, netWeight);

            long result = db.insertOrThrow(TABLE_QUANTITIES, null, values);
            db.setTransactionSuccessful();
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding quantity: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    private double calculateNetWeight(double totalWeight, double bagWeight,
                                      double moistureWeight, double otherDeductions) {
        return totalWeight - (bagWeight + moistureWeight + otherDeductions);
    }

    public Cursor getAllQuantities() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT q.*, f." + COLUMN_NAME + " as farmer_name, r." + COLUMN_NAME + " as rice_type, " +
                "q." + CREATED_AT + " as created_date " +
                "FROM " + TABLE_QUANTITIES + " q " +
                "JOIN " + TABLE_FARMERS + " f ON q." + FARMER_ID + " = f." + COLUMN_ID + " " +
                "JOIN " + TABLE_RICE_TYPES + " r ON q." + RICE_TYPE_ID + " = r." + COLUMN_ID + " " +
                "ORDER BY q." + CREATED_AT + " DESC";
        return db.rawQuery(query, null);
    }
}