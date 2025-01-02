package com.example.harvestflow;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harvestflow.Database.FarmerDatabaseHelper;

import java.util.List;
import java.util.HashMap;

public class FarmerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_list);

        ListView farmerListView = findViewById(R.id.farmerListView);

        // Initialize the database helper and get all farmers
        FarmerDatabaseHelper dbHelper = new FarmerDatabaseHelper(this);
        List<HashMap<String, String>> farmerList = dbHelper.getAllFarmers();

        // Set up the adapter for the ListView
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                farmerList,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "location"}, // Mapping columns
                new int[]{android.R.id.text1, android.R.id.text2} // Mapping TextViews
        );

        farmerListView.setAdapter(adapter);
    }
}
