package com.example.harvestflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class CollectorDashboardActivity extends AppCompatActivity {
    private String username;
    private String collectorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_dashboard);

        // Get username from intent (keeping your existing implementation)
        username = getIntent().getStringExtra("username");
        collectorId = getIntent().getStringExtra("id");

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Welcome, " + username);
        }

        // Initialize dashboard cards
        CardView farmersCard = findViewById(R.id.card_farmers);
        CardView riceTypesCard = findViewById(R.id.card_rice_types);
        CardView quantitiesCard = findViewById(R.id.card_quantities);
        CardView reportsCard = findViewById(R.id.card_reports);

        // Set click listeners for navigation
        farmersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectorDashboardActivity.this, FarmerManagementActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("collector_id", collectorId);
                startActivity(intent);
            }
        });

        riceTypesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectorDashboardActivity.this, RiceTypesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        quantitiesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectorDashboardActivity.this, QuantitiesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        reportsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectorDashboardActivity.this, ReportsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }
}