package com.example.harvestflow.farmermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.harvestflow.Database.FarmerDatabaseHelper;
import com.example.harvestflow.R;

public class FarmerManagementActivity extends AppCompatActivity {
    private FarmerDatabaseHelper dbHelper;
    private int collectorId;
    //Create new farmer and retrieve the farmer list
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_management);
        setupBackButton();

        dbHelper = new FarmerDatabaseHelper(this);
        collectorId = getIntent().getIntExtra("collector_id", -1);

        EditText name = findViewById(R.id.farmerName);
        EditText nic = findViewById(R.id.farmerNic);
        EditText location = findViewById(R.id.farmerLocation);
        EditText age = findViewById(R.id.farmerAge);
        EditText email = findViewById(R.id.farmerEmail);
        EditText phone = findViewById(R.id.farmerPhone);
        EditText landSize = findViewById(R.id.farmerLandSize);
        Button registerButton = findViewById(R.id.registerFarmerButton);
        Button viewFarmersButton = findViewById(R.id.viewFarmersButton);

        registerButton.setOnClickListener(v -> {
            if (validateInputs(name, nic, location, age, email, phone, landSize)) {
                String farmerName = name.getText().toString().trim();
                String farmerNic = nic.getText().toString().trim();
                String farmerLocation = location.getText().toString().trim();
                int farmerAge = Integer.parseInt(age.getText().toString().trim());
                String farmerEmail = email.getText().toString().trim();
                String farmerPhone = phone.getText().toString().trim();
                double farmerLandSize = Double.parseDouble(landSize.getText().toString().trim());

                if (dbHelper.addFarmer(farmerName, farmerNic, farmerLocation, farmerAge,
                        farmerEmail, farmerPhone, collectorId, farmerLandSize)) {
                    Toast.makeText(this, "Farmer Added Successfully", Toast.LENGTH_SHORT).show();
                    clearFields(name, nic, location, age, email, phone, landSize);
                } else {
                    Toast.makeText(this, "Failed to Add Farmer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewFarmersButton.setOnClickListener(v -> {
            Intent intent = new Intent(FarmerManagementActivity.this, FarmerListActivity.class);
            intent.putExtra("collector_id", collectorId);
            startActivity(intent);
        });
    }
    //Handling validation
    private boolean validateInputs(EditText name, EditText nic, EditText location,
                                   EditText age, EditText email, EditText phone, EditText landSize) {
        boolean isValid = true;

        if (name.getText().toString().trim().isEmpty()) {
            name.setError("Name is required");
            isValid = false;
        }

        String nicText = nic.getText().toString().trim().toUpperCase();
        if (!isValidSriLankanNIC(nicText)) {
            nic.setError("Invalid NIC format");
            isValid = false;
        }

        if (location.getText().toString().trim().isEmpty()) {
            location.setError("Location is required");
            isValid = false;
        }

        String ageText = age.getText().toString().trim();
        if (ageText.isEmpty() || !isValidAge(ageText)) {
            age.setError("Age must be between 18 and 100");
            isValid = false;
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

        String landSizeText = landSize.getText().toString().trim();
        if (landSizeText.isEmpty() || !isValidLandSize(landSizeText)) {
            landSize.setError("Invalid land size");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidSriLankanNIC(String nic) {
        String oldNICPattern = "^\\d{9}[VX]$";
        String newNICPattern = "^\\d{12}[VX]$";
        return nic.matches(oldNICPattern) || nic.matches(newNICPattern);
    }

    private boolean isValidAge(String age) {
        try {
            int ageValue = Integer.parseInt(age);
            return ageValue >= 18 && ageValue <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidLandSize(String landSize) {
        try {
            double size = Double.parseDouble(landSize);
            return size > 0 && size <= 1000;
        } catch (NumberFormatException e) {
            return false;
        }
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