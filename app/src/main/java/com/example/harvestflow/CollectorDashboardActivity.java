package com.example.harvestflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.harvestflow.Database.CollectorDatabaseHelper;
import com.example.harvestflow.farmermanagement.FarmerManagementActivity;
import com.example.harvestflow.ricetypemanagement.RiceTypesActivity;
import com.example.harvestflow.millmanagement.RiceMillManagementActivity;
import com.example.harvestflow.millmanagement.SendToMillsActivity;

public class CollectorDashboardActivity extends AppCompatActivity {
    private String username;
    private String collectorId;
    private CollectorDatabaseHelper dbHelper;

    // UI Components
    private TextView welcomeText;
    private Toolbar toolbar;
    private ImageButton logoutButton;
    private CardView farmersCard, riceTypesCard, quantitiesCard, reportsCard, riceMillsCard, sendToMillsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector_dashboard);

        // Initialize database helper
        dbHelper = new CollectorDatabaseHelper(this);

        // Get intent extras
        getIntentExtras();

        // Initialize UI components
        initializeViews();

        // Setup toolbar and welcome message
        setupToolbarAndWelcome();

        // Setup logout button
        setupLogoutButton();

        // Setup dashboard cards
        setupDashboardCards();
    }

    private void getIntentExtras() {
        username = getIntent().getStringExtra("username");
        collectorId = getIntent().getStringExtra("id");
    }

    private void initializeViews() {
        // Initialize toolbar and common elements
        toolbar = findViewById(R.id.toolbar);
        welcomeText = findViewById(R.id.tv_welcome);
        logoutButton = findViewById(R.id.btn_logout);

        // Initialize all card views
        farmersCard = findViewById(R.id.card_farmers);
        riceTypesCard = findViewById(R.id.card_rice_types);
        quantitiesCard = findViewById(R.id.card_quantities);
        reportsCard = findViewById(R.id.card_reports);
        riceMillsCard = findViewById(R.id.card_rice_mills);
        sendToMillsCard = findViewById(R.id.card_send_to_mills);
    }

    private void setupToolbarAndWelcome() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
        }

        String collectorName = dbHelper.getCollectorName(username);
        welcomeText.setText("Welcome, " + collectorName);
    }

    private void setupLogoutButton() {
        logoutButton.setOnClickListener(v -> logout());
    }

    private void setupDashboardCards() {
        // Farmers Card
        farmersCard.setOnClickListener(v -> navigateToFarmerManagement());

        // Rice Types Card
        riceTypesCard.setOnClickListener(v -> navigateToRiceTypes());

        // Quantities Card
        quantitiesCard.setOnClickListener(v -> navigateToQuantities());

        // Reports Card
        reportsCard.setOnClickListener(v -> navigateToReports());

        // Rice Mills Card
        riceMillsCard.setOnClickListener(v -> navigateToRiceMills());

        // Send to Mills Card
        sendToMillsCard.setOnClickListener(v -> navigateToSendToMills());
    }

    // Navigation Methods
    private void navigateToFarmerManagement() {
        Intent intent = new Intent(this, FarmerManagementActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("collector_id", collectorId);
        startActivity(intent);
    }

    private void navigateToRiceTypes() {
        Intent intent = new Intent(this, RiceTypesActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void navigateToQuantities() {
        Intent intent = new Intent(this, QuantitiesActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void navigateToReports() {
        Intent intent = new Intent(this, ReportsActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void navigateToRiceMills() {
        Intent intent = new Intent(this, RiceMillManagementActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("collector_id", collectorId);
        startActivity(intent);
    }

    private void navigateToSendToMills() {
        Intent intent = new Intent(this, SendToMillsActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("collector_id", collectorId);
        startActivity(intent);
    }

    private void logout() {
        // Clear any saved credentials or session data if needed
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}