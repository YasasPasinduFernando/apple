package com.example.harvestflow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;

import com.example.harvestflow.Database.CollectorDatabaseHelper;

import java.util.Random;


public class AdminPanelActivity extends AppCompatActivity {

    CollectorDatabaseHelper dbHelper;
    private int collectorIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        dbHelper = new CollectorDatabaseHelper(this);

        EditText name = findViewById(R.id.collectorName);
        EditText nic = findViewById(R.id.collectorNic);
        EditText location = findViewById(R.id.collectorLocation);
        EditText age = findViewById(R.id.collectorAge);
        EditText email = findViewById(R.id.collectorEmail);
        EditText phone = findViewById(R.id.collectorPhone);
        Button registerButton = findViewById(R.id.registerCollectorButton);

        registerButton.setOnClickListener(v -> {
            if (validateInputs(name, nic, location, age, email, phone)) {
                String collectorName = name.getText().toString().trim();
                String collectorNic = nic.getText().toString().trim();
                String collectorLocation = location.getText().toString().trim();
                int collectorAge = Integer.parseInt(age.getText().toString().trim());
                String collectorEmail = email.getText().toString().trim();
                String collectorPhone = phone.getText().toString().trim();

                // Generate username based on NIC (using first part before V/X)
                String username = "COL" + collectorNic.substring(0, Math.min(4, collectorNic.length() - 1));
                // Generate random password
                String password = generatePassword(collectorNic);

                // Save Collector to Database
                if (dbHelper.addCollector(collectorName, collectorNic, collectorLocation, collectorAge,
                        collectorEmail, collectorPhone, username, password)) {
                    showCredentialsDialog(username, password);
                    clearFields(name, nic, location, age, email, phone);
                } else {
                    Toast.makeText(AdminPanelActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs(EditText name, EditText nic, EditText location,
                                   EditText age, EditText email, EditText phone) {
        boolean isValid = true;

        // Name validation
        if (name.getText().toString().trim().isEmpty()) {
            name.setError("Name is required");
            isValid = false;
        }

        // NIC validation
        String nicText = nic.getText().toString().trim().toUpperCase();
        if (!isValidSriLankanNIC(nicText)) {
            nic.setError("Invalid NIC format (must end with V or X)");
            isValid = false;
        }

        // Location validation
        if (location.getText().toString().trim().isEmpty()) {
            location.setError("Location is required");
            isValid = false;
        }

        // Age validation
        String ageText = age.getText().toString().trim();
        if (ageText.isEmpty()) {
            age.setError("Age is required");
            isValid = false;
        } else {
            try {
                int ageValue = Integer.parseInt(ageText);
                if (ageValue < 18 || ageValue > 70) {
                    age.setError("Age must be between 18 and 70");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                age.setError("Invalid age");
                isValid = false;
            }
        }

        // Email validation
        String emailText = email.getText().toString().trim();
        if (!emailText.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid email format");
            isValid = false;
        }

        // Phone validation
        String phoneText = phone.getText().toString().trim();
        if (!phoneText.matches("0\\d{9}")) {
            phone.setError("Phone must be 10 digits starting with 0");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidSriLankanNIC(String nic) {
        // Old NIC format: 9 digits + V/X
        // New NIC format: 12 digits + V/X
        String oldNICPattern = "^\\d{9}[VX]$";
        String newNICPattern = "^\\d{12}[VX]$";

        return nic.matches(oldNICPattern) || nic.matches(newNICPattern);
    }

    private String generatePassword(String nic) {
        // Generate a random password using NIC and random characters
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        // Use the first 4 digits of NIC (before V/X)
        password.append(nic.substring(0, 4));

        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    private void clearFields(EditText... fields) {
        for (EditText field : fields) {
            field.setText("");
        }
    }

    private void showCredentialsDialog(String username, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Collector Registered Successfully");
        builder.setMessage("Username: " + username + "\nPassword: " + password);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Copy", (dialog, which) -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Collector Credentials",
                    "Username: " + username + "\nPassword: " + password);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(AdminPanelActivity.this, "Credentials Copied to Clipboard", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
}


