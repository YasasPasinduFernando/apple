package com.example.harvestflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import com.example.harvestflow.Database.CollectorDatabaseHelper;
import java.util.Random;

public class AdminPanelActivity extends AppCompatActivity {
    private CollectorDatabaseHelper dbHelper;
    private EditText name, nic, location, age, email, phone;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        dbHelper = new CollectorDatabaseHelper(this);
        name = findViewById(R.id.collectorName);
        nic = findViewById(R.id.collectorNic);
        location = findViewById(R.id.collectorLocation);
        age = findViewById(R.id.collectorAge);
        email = findViewById(R.id.collectorEmail);
        phone = findViewById(R.id.collectorPhone);
        registerButton = findViewById(R.id.registerCollectorButton);
    }

    private void setupListeners() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(AdminPanelActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });

        registerButton.setOnClickListener(v -> registerCollector());
    }

    private void registerCollector() {
        try {
            if (validateInputs(name, nic, location, age, email, phone)) {
                String collectorName = name.getText().toString().trim();
                String collectorNic = nic.getText().toString().trim().toUpperCase();
                String collectorLocation = location.getText().toString().trim();
                int collectorAge = Integer.parseInt(age.getText().toString().trim());
                String collectorEmail = email.getText().toString().trim();
                String collectorPhone = phone.getText().toString().trim();

                String username = generateUsername(collectorNic);
                String password = generatePassword(collectorNic);

                if (dbHelper.addCollector(collectorName, collectorNic, collectorLocation, collectorAge,
                        collectorEmail, collectorPhone, username, password)) {
                    showCredentialsDialog(username, password);
                    clearFields(name, nic, location, age, email, phone);
                } else {
                    Toast.makeText(this, "Registration Failed - NIC may already exist", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Registration Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    //Generate the username
    private String generateUsername(String nic) {
        return "COL" + nic.substring(0, Math.min(4, nic.length() - 1));
    }

    private boolean validateInputs(EditText name, EditText nic, EditText location,
                                   EditText age, EditText email, EditText phone) {
        boolean isValid = true;

        if (name.getText().toString().trim().isEmpty()) {
            name.setError("Name is required");
            isValid = false;
        }

        String nicText = nic.getText().toString().trim().toUpperCase();
        if (!isValidSriLankanNIC(nicText)) {
            nic.setError("Invalid NIC format (must end with V or X)");
            isValid = false;
        }

        if (location.getText().toString().trim().isEmpty()) {
            location.setError("Location is required");
            isValid = false;
        }

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

        String emailText = email.getText().toString().trim();
        if (!emailText.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid email format");
            isValid = false;
        }

        String phoneText = phone.getText().toString().trim();
        if (!phoneText.matches("0\\d{9}")) {
            phone.setError("Phone must be 10 digits starting with 0");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidSriLankanNIC(String nic) {
        return nic.matches("^\\d{9}[VX]$") || nic.matches("^\\d{12}[VX]$");
    }
    //Generate the password
    private String generatePassword(String nic) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder(nic.substring(0, 4));
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
    //Credential clipboard copy
    private void showCredentialsDialog(String username, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Collector Registered Successfully")
                .setMessage("Username: " + username + "\nPassword: " + password)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Copy", (dialog, which) -> {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Collector Credentials",
                            "Username: " + username + "\nPassword: " + password);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, "Credentials Copied to Clipboard", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}