package com.example.harvestflow.millmanagement;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendToMillsActivity extends AppCompatActivity {
    private String username;
    private AutoCompleteTextView spinnerMill, spinnerRiceType;
    private EditText etQuantity;
    private Button btnSend;

    private RiceMillDatabaseHelper riceMillDb;
    private RiceTypeDatabaseHelper riceTypeDb;
    private ApprovedQuantitiesDatabaseHelper approvedQuantitiesDb;

    private List<HashMap<String, String>> riceMills = new ArrayList<>();
    private List<HashMap<String, String>> riceTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_mills);

        // Initialize views and database
        initializeViews();
        setupToolbar();
        username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {
            showError("Username not provided");
            return;
        }

        initializeDatabases();

        if (!loadDatabaseData()) {
            showError("Failed to load data from the database");
            return;
        }

        setupDropdowns();
        setupSendButton();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initializeViews() {
        spinnerMill = findViewById(R.id.spinner_mill);
        spinnerRiceType = findViewById(R.id.spinner_rice_type);
        etQuantity = findViewById(R.id.et_quantity);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Send to Mills");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeDatabases() {
        riceMillDb = new RiceMillDatabaseHelper(this);
        riceTypeDb = new RiceTypeDatabaseHelper(this);
        approvedQuantitiesDb = new ApprovedQuantitiesDatabaseHelper(this);
    }

    private boolean loadDatabaseData() {
        try {
            riceMills = riceMillDb.getAllRiceMills();
            riceTypes = riceTypeDb.getAllRiceTypes();
        } catch (Exception e) {
            return false; // Return false if there's a database conflict or failure
        }

        return !riceMills.isEmpty() && !riceTypes.isEmpty();
    }

    private void setupDropdowns() {
        List<String> millNames = new ArrayList<>();
        for (HashMap<String, String> mill : riceMills) {
            millNames.add(mill.get("name"));
        }
        ArrayAdapter<String> millAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, millNames);
        spinnerMill.setAdapter(millAdapter);

        List<String> riceTypeNames = new ArrayList<>();
        for (HashMap<String, String> riceType : riceTypes) {
            riceTypeNames.add(riceType.get("name"));
        }
        ArrayAdapter<String> riceTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, riceTypeNames);
        spinnerRiceType.setAdapter(riceTypeAdapter);
    }

    private boolean validateInputs() {
        String selectedMill = spinnerMill.getText().toString();
        String selectedRiceType = spinnerRiceType.getText().toString();
        String quantityStr = etQuantity.getText().toString();

        if (selectedMill.isEmpty() || selectedRiceType.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupSendButton() {
        btnSend.setOnClickListener(v -> {
            if (validateInputs()) {
                sendToMill();
            }
        });
    }

    private void sendToMill() {
        String millId = getSelectedMillId();
        String riceTypeId = getSelectedRiceTypeId();

        if (millId == null || riceTypeId == null) {
            Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity = Double.parseDouble(etQuantity.getText().toString());
        boolean success = approvedQuantitiesDb.addApprovedQuantity(
                Integer.parseInt(millId),
                Integer.parseInt(riceTypeId),
                quantity,
                username);

        if (success) {
            spinnerMill.setText("");
            spinnerRiceType.setText("");
            etQuantity.setText("");
            Toast.makeText(this, "Successfully sent to mill!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to send to mill", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedMillId() {
        String selectedMill = spinnerMill.getText().toString();
        for (HashMap<String, String> mill : riceMills) {
            if (selectedMill.equals(mill.get("name"))) {
                return mill.get("id");
            }
        }
        return null;
    }

    private String getSelectedRiceTypeId() {
        String selectedRiceType = spinnerRiceType.getText().toString();
        for (HashMap<String, String> riceType : riceTypes) {
            if (selectedRiceType.equals(riceType.get("name"))) {
                return riceType.get("id");
            }
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
