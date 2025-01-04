package com.example.harvestflow.millmanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.harvestflow.Database.ApprovedQuantitiesDatabaseHelper;
import com.example.harvestflow.Database.RiceMillDatabaseHelper;
import com.example.harvestflow.Database.RiceTypeDatabaseHelper;
import com.example.harvestflow.R;

import java.util.HashMap;
import java.util.List;

public class SendToMillsActivity extends AppCompatActivity {
    private static final String TAG = "SendToMillsActivity";
    private String username;
    private AutoCompleteTextView spinnerMill, spinnerRiceType;
    private EditText etQuantity;
    private Button btnSend;

    private RiceMillDatabaseHelper riceMillDb;
    private RiceTypeDatabaseHelper riceTypeDb;
    private ApprovedQuantitiesDatabaseHelper approvedQuantitiesDb;

    private List<HashMap<String, String>> riceMills;
    private List<HashMap<String, String>> riceTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_mills);

        // Get username from intent
        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: Username not provided", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeDatabases();
        setupToolbar();
        initializeViews();
        setupDropdowns();
        setupSendButton();
    }

    private void initializeDatabases() {
        try {
            riceMillDb = new RiceMillDatabaseHelper(this);
            riceTypeDb = new RiceTypeDatabaseHelper(this);
            approvedQuantitiesDb = new ApprovedQuantitiesDatabaseHelper(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing databases: " + e.getMessage());
            Toast.makeText(this, "Error initializing databases", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Send to Mills");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        spinnerMill = findViewById(R.id.spinner_mill);
        spinnerRiceType = findViewById(R.id.spinner_rice_type);
        etQuantity = findViewById(R.id.et_quantity);
        btnSend = findViewById(R.id.btn_send);

        spinnerMill.setFocusable(false);
        spinnerRiceType.setFocusable(false);
    }

    private void setupDropdowns() {
        try {
            // Get data from databases
            riceMills = riceMillDb.getAllRiceMills();
            riceTypes = riceTypeDb.getAllRiceTypes();

            if (riceMills.isEmpty()) {
                Toast.makeText(this, "No mills available", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (riceTypes.isEmpty()) {
                Toast.makeText(this, "No rice types available", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Setup mill spinner
            String[] millNames = new String[riceMills.size()];
            for (int i = 0; i < riceMills.size(); i++) {
                millNames[i] = riceMills.get(i).get("name");
            }
            ArrayAdapter<String> millAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    millNames);
            spinnerMill.setAdapter(millAdapter);

            // Setup rice type spinner
            String[] riceTypeNames = new String[riceTypes.size()];
            for (int i = 0; i < riceTypes.size(); i++) {
                riceTypeNames[i] = riceTypes.get(i).get("name");
            }
            ArrayAdapter<String> riceTypeAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    riceTypeNames);
            spinnerRiceType.setAdapter(riceTypeAdapter);

            // Enable dropdown functionality
            spinnerMill.setOnClickListener(v -> spinnerMill.showDropDown());
            spinnerRiceType.setOnClickListener(v -> spinnerRiceType.showDropDown());
        } catch (Exception e) {
            Log.e(TAG, "Error setting up dropdowns: " + e.getMessage());
            Toast.makeText(this, "Error loading data", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private boolean validateInputs() {
        String selectedMill = spinnerMill.getText().toString();
        String selectedRiceType = spinnerRiceType.getText().toString();
        String quantityStr = etQuantity.getText().toString();

        if (selectedMill.isEmpty()) {
            spinnerMill.setError("Please select a mill");
            return false;
        }

        if (selectedRiceType.isEmpty()) {
            spinnerRiceType.setError("Please select a rice type");
            return false;
        }

        if (quantityStr.isEmpty()) {
            etQuantity.setError("Please enter quantity");
            return false;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                etQuantity.setError("Quantity must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid quantity format");
            return false;
        }

        return true;
    }

    private void sendToMill() {
        try {
            // Get selected mill ID
            int millId = -1;
            String selectedMill = spinnerMill.getText().toString();
            for (HashMap<String, String> mill : riceMills) {
                if (mill.get("name").equals(selectedMill)) {
                    millId = Integer.parseInt(mill.get("id"));
                    break;
                }
            }

            // Get selected rice type ID
            int riceTypeId = -1;
            String selectedRiceType = spinnerRiceType.getText().toString();
            for (HashMap<String, String> riceType : riceTypes) {
                if (riceType.get("name").equals(selectedRiceType)) {
                    riceTypeId = Integer.parseInt(riceType.get("id"));
                    break;
                }
            }

            double quantity = Double.parseDouble(etQuantity.getText().toString());

            // Save to database
            boolean success = approvedQuantitiesDb.addApprovedQuantity(
                    millId, riceTypeId, quantity, username);

            if (success) {
                // Clear all fields
                spinnerMill.setText("");
                spinnerRiceType.setText("");
                etQuantity.setText("");

                // Show success message
                Toast.makeText(this, "Rice quantity successfully sent to mill!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Rice quantity successfully sent to mill", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error processing request. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSendButton() {
        btnSend.setOnClickListener(v -> {
            if (validateInputs()) {
                sendToMill();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}