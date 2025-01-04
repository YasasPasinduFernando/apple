package com.example.harvestflow;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harvestflow.Database.ApprovedQuantitiesDatabaseHelper;
import com.example.harvestflow.Adapters.ApprovedQuantitiesAdapter;

import java.util.HashMap;
import java.util.List;

public class ViewApprovedQuantitiesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView totalQuantityTv;
    private ApprovedQuantitiesAdapter adapter;
    private ApprovedQuantitiesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_approved_quantities);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Approved Quantities");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view);
        totalQuantityTv = findViewById(R.id.tv_total_quantity);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database helper
        dbHelper = new ApprovedQuantitiesDatabaseHelper(this);

        // Load and display data
        loadData();
    }

    private void loadData() {
        // Get total quantity
        double totalQuantity = dbHelper.getTotalQuantity();
        totalQuantityTv.setText(String.format("Total: %.2f kg", totalQuantity));

        // Get all approved quantities
        List<HashMap<String, String>> approvedQuantities = dbHelper.getAllApprovedQuantities();

        // Setup or update adapter
        if (adapter == null) {
            adapter = new ApprovedQuantitiesAdapter(approvedQuantities);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(approvedQuantities);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}