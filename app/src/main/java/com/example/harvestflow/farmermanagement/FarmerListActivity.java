package com.example.harvestflow.farmermanagement;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.example.harvestflow.Database.FarmerDatabaseHelper;
import com.example.harvestflow.R;
import java.util.HashMap;
import java.util.List;

public class FarmerListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_list);

        ListView farmerListView = findViewById(R.id.farmerListView);
        ImageButton backButton = findViewById(R.id.backButton);

        // Set up the back button functionality
        backButton.setOnClickListener(v -> onBackPressed());

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
