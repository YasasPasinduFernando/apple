package com.example.harvestflow.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuantityHelperDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "harvest_flow.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_QUANTITIES = "quantities";
    private static final String QUANTITY_ID = "id";
    private static final String FARMER_ID = "farmer_id";
    private static final String RICE_TYPE_ID = "rice_type_id";
    private static final String BAG_WEIGHT = "bag_weight";
    private static final String MOISTURE_WEIGHT = "moisture_weight";
    private static final String OTHER_DEDUCTIONS = "other_deductions";
    private static final String NET_WEIGHT = "net_weight";

    public QuantityHelperDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_QUANTITIES_TABLE = "CREATE TABLE " + TABLE_QUANTITIES + "(" +
                QUANTITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FARMER_ID + " INTEGER, " +
                RICE_TYPE_ID + " INTEGER, " +
                BAG_WEIGHT + " REAL, " +
                MOISTURE_WEIGHT + " REAL, " +
                OTHER_DEDUCTIONS + " REAL, " +
                NET_WEIGHT + " REAL, " +
                "FOREIGN KEY(" + FARMER_ID + ") REFERENCES farmers(id), " +
                "FOREIGN KEY(" + RICE_TYPE_ID + ") REFERENCES rice_types(id)" +
                ")";
        db.execSQL(CREATE_QUANTITIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANTITIES);
        onCreate(db);
    }

    public boolean addQuantity(int farmerId, int riceTypeId, double bagWeight, double moistureWeight, double otherDeductions) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        double netWeight = bagWeight - (moistureWeight + otherDeductions);

        values.put(FARMER_ID, farmerId);
        values.put(RICE_TYPE_ID, riceTypeId);
        values.put(BAG_WEIGHT, bagWeight);
        values.put(MOISTURE_WEIGHT, moistureWeight);
        values.put(OTHER_DEDUCTIONS, otherDeductions);
        values.put(NET_WEIGHT, netWeight);

        long result = db.insert(TABLE_QUANTITIES, null, values);
        return result != -1;
    }

    public Cursor getAllQuantities() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT q." + QUANTITY_ID + ", f.name AS farmer_name, r.name AS rice_name, q." +
                BAG_WEIGHT + ", q." + MOISTURE_WEIGHT + ", q." + OTHER_DEDUCTIONS + ", q." + NET_WEIGHT +
                " FROM " + TABLE_QUANTITIES + " q " +
                "JOIN farmers f ON q." + FARMER_ID + " = f.id " +
                "JOIN rice_types r ON q." + RICE_TYPE_ID + " = r.id", null);
    }
}
