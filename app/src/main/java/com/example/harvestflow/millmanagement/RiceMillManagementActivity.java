package com.example.harvestflow.millmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.harvestflow.Database.RiceMillDatabaseHelper;
import com.example.harvestflow.R;
import com.example.harvestflow.millmanagement.RiceMillListActivity;

public class RiceMillManagementActivity extends AppCompatActivity {
    private RiceMillDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricemill_management);
        setupBackButton();

        dbHelper = new RiceMillDatabaseHelper(this);

        EditText name = findViewById(R.id.millName);
        EditText location = findViewById(R.id.millLocation);
        EditText contact = findViewById(R.id.ownerContact);
        EditText regId = findViewById(R.id.businessRegId);
        Button registerButton = findViewById(R.id.registerMillButton);
        Button viewMillsButton = findViewById(R.id.viewMillsButton);

        registerButton.setOnClickListener(v -> {
            if (validateInputs(name, location, contact, regId)) {
                String millName = name.getText().toString().trim();
                String millLocation = location.getText().toString().trim();
                String ownerContact = formatPhoneNumber(contact.getText().toString().trim());
                String businessRegId = regId.getText().toString().trim().toUpperCase();

                try {
                    if (dbHelper.addRiceMill(millName, millLocation, ownerContact, businessRegId)) {
                        Toast.makeText(this, "Rice Mill Added Successfully", Toast.LENGTH_SHORT).show();
                        clearFields(name, location, contact, regId);
                    } else {
                        Toast.makeText(this, "Business Registration ID already exists", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        viewMillsButton.setOnClickListener(v -> {
            Intent intent = new Intent(RiceMillManagementActivity.this, RiceMillListActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(EditText name, EditText location, EditText contact, EditText regId) {
        boolean isValid = true;

        if (name.getText().toString().trim().isEmpty()) {
            name.setError("Mill name is required");
            isValid = false;
        }

        if (location.getText().toString().trim().isEmpty()) {
            location.setError("Location is required");
            isValid = false;
        }

        String contactText = contact.getText().toString().trim();
        if (contactText.isEmpty()) {
            contact.setError("Contact number is required");
            isValid = false;
        } else if (!isValidPhoneNumber(contactText)) {
            contact.setError("Enter a valid phone number (e.g., 0771234567 or +94771234567)");
            isValid = false;
        }

        String regIdText = regId.getText().toString().trim().toUpperCase();
        if (!isValidBusinessRegId(regIdText)) {
            regId.setError("Invalid format. Use BRGyyyyXXX (e.g., BRG2024001)");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidBusinessRegId(String regId) {
        // Format: BRGyyyyXXX where yyyy is year and XXX is a sequence number
        return regId.matches("BRG\\d{4}\\d{3}");
    }

    private boolean isValidPhoneNumber(String phone) {
        // Accept both formats: 0771234567 or +94771234567
        return phone.matches("0\\d{9}") || phone.matches("\\+94\\d{9}");
    }

    private String formatPhoneNumber(String phone) {
        // Convert local format to international format if needed
        if (phone.startsWith("0")) {
            return "+94" + phone.substring(1);
        }
        return phone;
    }

    private void clearFields(EditText... fields) {
        for (EditText field : fields) {
            field.setText("");
        }
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}