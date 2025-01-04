package com.example.harvestflow;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harvestflow.Database.FarmerDatabaseHelper;
import com.example.harvestflow.Database.QuantityHelperDatabase;
import com.example.harvestflow.Database.RiceTypeDatabaseHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    private Spinner spinnerFarmer, spinnerRiceType;
    private BarChart barChart;
    private QuantityHelperDatabase quantityDb;
    private FarmerDatabaseHelper farmerDb;
    private RiceTypeDatabaseHelper riceTypeDb;

    private static final int ANIMATION_DURATION = 1000;
    private static final float BAR_WIDTH = 0.85f;
    private static final int CHART_BAR_COLOR = Color.rgb(98, 0, 238);
    private static final int CHART_TEXT_COLOR = Color.BLACK;
    private static final int CHART_GRID_COLOR = Color.LTGRAY;
    private static final int CHART_AXIS_COLOR = Color.DKGRAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        // Initialize back button and set the listener
        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish()); // Closes the current activity

        initializeViews();
        setupDatabases();
        setupChartDefaults();
        setupSpinners();
        updateChart();
    }

    private void initializeViews() {
        try {
            spinnerFarmer = findViewById(R.id.spinner_farmer);
            spinnerRiceType = findViewById(R.id.spinner_rice_type);
            barChart = findViewById(R.id.bar_chart);

            if (spinnerFarmer == null || spinnerRiceType == null || barChart == null) {
                throw new IllegalStateException("One or more views not found");
            }
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error initializing views: " + e.getMessage());
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupDatabases() {
        try {
            quantityDb = new QuantityHelperDatabase(this);
            farmerDb = new FarmerDatabaseHelper(this);
            riceTypeDb = new RiceTypeDatabaseHelper(this);
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error setting up databases: " + e.getMessage());
            Toast.makeText(this, "Error connecting to databases", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupChartDefaults() {
        try {
            barChart.setDrawGridBackground(false);
            barChart.setDrawBorders(false);

            Description description = new Description();
            description.setText("Harvest Quantities");
            description.setTextColor(CHART_TEXT_COLOR);
            barChart.setDescription(description);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setDrawGridLines(false);
            xAxis.setTextColor(CHART_AXIS_COLOR);
            xAxis.setLabelRotationAngle(45f);

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setDrawGridLines(true);
            leftAxis.setGridColor(CHART_GRID_COLOR);
            leftAxis.setTextColor(CHART_AXIS_COLOR);
            leftAxis.setSpaceTop(30f);

            barChart.getAxisRight().setEnabled(false);
            barChart.animateY(ANIMATION_DURATION);
            barChart.getLegend().setTextColor(CHART_TEXT_COLOR);

        } catch (Exception e) {
            Log.e("ReportsActivity", "Error setting up chart: " + e.getMessage());
            Toast.makeText(this, "Error setting up chart", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinners() {
        try {
            // Setup Farmer Spinner
            List<String> farmers = farmerDb.getAllFarmersFormatted();
            farmers.add(0, "All Farmers");
            ArrayAdapter<String> farmerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, farmers);
            spinnerFarmer.setAdapter(farmerAdapter);

            // Setup Rice Type Spinner
            List<String> riceTypes = riceTypeDb.getAllRiceTypesFormatted();
            riceTypes.add(0, "All Rice Types");
            ArrayAdapter<String> riceTypeAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, riceTypes);
            spinnerRiceType.setAdapter(riceTypeAdapter);

            // Setup listeners
            spinnerFarmer.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view,
                                           int position, long id) {
                    updateChart();
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });

            spinnerRiceType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view,
                                           int position, long id) {
                    updateChart();
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
        } catch (Exception e) {
            Log.e("ReportsActivity", "Error setting up spinners: " + e.getMessage());
            Toast.makeText(this, "Error setting up filters", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChart() {
        try {
            SQLiteDatabase quantityDb = this.quantityDb.getReadableDatabase();
            List<BarEntry> entries = new ArrayList<>();
            List<String> labels = new ArrayList<>();

            // Get selected values from spinners
            String selectedFarmer = spinnerFarmer.getSelectedItem().toString();
            String selectedRiceType = spinnerRiceType.getSelectedItem().toString();

            // Extract base names (without the location/type info)
            String farmerName = "All Farmers".equals(selectedFarmer) ? null :
                    selectedFarmer.substring(0, selectedFarmer.indexOf(" (")).trim();
            String riceTypeName = "All Rice Types".equals(selectedRiceType) ? null :
                    selectedRiceType.substring(0, selectedRiceType.indexOf(" (")).trim();

            // Start with base query
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM quantities");

            List<String> whereConditions = new ArrayList<>();
            List<String> whereArgs = new ArrayList<>();

            // Add conditions for filtering
            if (farmerName != null || riceTypeName != null) {
                queryBuilder.append(" WHERE ");

                if (farmerName != null) {
                    whereConditions.add("farmer_id IN (SELECT id FROM farmers WHERE name = ?)");
                    whereArgs.add(farmerName);
                }

                if (riceTypeName != null) {
                    whereConditions.add("rice_type_id IN (SELECT id FROM rice_types WHERE name = ?)");
                    whereArgs.add(riceTypeName);
                }

                queryBuilder.append(String.join(" AND ", whereConditions));
            }

            Log.d("ReportsActivity", "Executing query: " + queryBuilder.toString());
            Log.d("ReportsActivity", "With args: " + String.join(", ", whereArgs));

            Cursor cursor = quantityDb.rawQuery(queryBuilder.toString(),
                    whereArgs.isEmpty() ? null : whereArgs.toArray(new String[0]));

            Log.d("ReportsActivity", "Query returned " + cursor.getCount() + " rows");

            if (cursor.moveToFirst()) {
                int index = 0;
                do {
                    float netWeight = cursor.getFloat(cursor.getColumnIndex("net_weight"));
                    int farmerId = cursor.getInt(cursor.getColumnIndex("farmer_id"));
                    int riceTypeId = cursor.getInt(cursor.getColumnIndex("rice_type_id"));

                    // Get farmer and rice type details
                    String farmerInfo = getFarmerName(farmerId);
                    String riceTypeInfo = getRiceTypeName(riceTypeId);

                    entries.add(new BarEntry(index, netWeight));
                    labels.add(farmerInfo + "\n" + riceTypeInfo);
                    index++;
                } while (cursor.moveToNext());
            }
            cursor.close();

            if (entries.isEmpty()) {
                Log.d("ReportsActivity", "No entries found for selected filters");
                barChart.setNoDataText("No harvest data available for selected filters");
                barChart.invalidate();
                return;
            }

            // Create dataset
            BarDataSet dataSet = new BarDataSet(entries, "Net Weight (kg)");
            dataSet.setColor(CHART_BAR_COLOR);
            dataSet.setValueTextColor(CHART_TEXT_COLOR);
            dataSet.setValueTextSize(10f);

            BarData data = new BarData(dataSet);
            data.setBarWidth(BAR_WIDTH);

            // Configure X-axis
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int i = Math.round(value);
                    if (i >= 0 && i < labels.size()) {
                        return labels.get(i);
                    }
                    return "";
                }
            });

            barChart.setData(data);
            barChart.invalidate();

        } catch (Exception e) {
            Log.e("ReportsActivity", "Error updating chart: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Update these helper methods to include formatted names
    private String getFarmerName(int farmerId) {
        SQLiteDatabase db = farmerDb.getReadableDatabase();
        String formattedName = "Unknown";
        Cursor cursor = db.query("farmers",
                new String[]{"name", "location"},
                "id = ?",
                new String[]{String.valueOf(farmerId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            formattedName = name + " (" + location + ")";
        }
        cursor.close();
        return formattedName;
    }

    private String getRiceTypeName(int riceTypeId) {
        SQLiteDatabase db = riceTypeDb.getReadableDatabase();
        String formattedName = "Unknown";
        Cursor cursor = db.query("rice_types",
                new String[]{"name", "type"},
                "id = ?",
                new String[]{String.valueOf(riceTypeId)},
                null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            formattedName = name + " (" + type + ")";
        }
        cursor.close();
        return formattedName;
    }

    private void updateChartWithSampleData(List<BarEntry> entries, List<String> labels) {
        if (entries.isEmpty()) {
            barChart.setNoDataText("No data available");
            barChart.invalidate();
            return;
        }

        // Create BarDataSet
        BarDataSet dataSet = new BarDataSet(entries, "Net Weight (kg)");
        dataSet.setColor(CHART_BAR_COLOR);
        dataSet.setValueTextColor(CHART_TEXT_COLOR);
        dataSet.setValueTextSize(10f);

        // Set data and configure X-axis labels
        BarData data = new BarData(dataSet);
        data.setBarWidth(BAR_WIDTH);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });

        barChart.setData(data);
        barChart.invalidate();
    }


    private void updateChartWithData(Cursor cursor) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int netWeightIndex = cursor.getColumnIndex("net_weight");
        int farmerNameIndex = cursor.getColumnIndex("farmer_name");
        int riceTypeIndex = cursor.getColumnIndex("rice_type");

        if (netWeightIndex == -1 || farmerNameIndex == -1 || riceTypeIndex == -1) {
            Toast.makeText(this, "Error: Invalid database structure", Toast.LENGTH_SHORT).show();
            return;
        }

        int index = 0;
        while (cursor.moveToNext() && index < 50) {
            try {
                float netWeight = cursor.getFloat(netWeightIndex);
                String farmerName = cursor.getString(farmerNameIndex);
                String riceType = cursor.getString(riceTypeIndex);

                entries.add(new BarEntry(index, netWeight));
                labels.add(farmerName + "\n" + riceType);
                index++;

            } catch (Exception e) {
                Log.e("ReportsActivity", "Error processing data for chart: " + e.getMessage());
            }
        }

        if (entries.isEmpty()) {
            barChart.setNoDataText("No data available");
            barChart.invalidate();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Net Weight (kg)");
        dataSet.setColor(CHART_BAR_COLOR);
        dataSet.setValueTextColor(CHART_TEXT_COLOR);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(BAR_WIDTH);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = Math.round(value);
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });

        barChart.setData(data);
        barChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (quantityDb != null) {
            quantityDb.close();
        }
        if (farmerDb != null) {
            farmerDb.close();
        }
        if (riceTypeDb != null) {
            riceTypeDb.close();
        }
    }
}