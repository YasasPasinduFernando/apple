package com.example.harvestflow.ricetypemanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.harvestflow.Database.RiceTypeDatabaseHelper;
import com.example.harvestflow.R;

public class RiceTypesActivity extends AppCompatActivity {
    private RiceTypeDatabaseHelper dbHelper;
    private EditText riceNameInput;
    private EditText riceTypeInput;
    private EditText pricePerKgInput;
    private Button saveButton;
    private Button viewRiceTypesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rice_types);

        dbHelper = new RiceTypeDatabaseHelper(this);

        riceNameInput = findViewById(R.id.riceName);
        riceTypeInput = findViewById(R.id.riceType);
        pricePerKgInput = findViewById(R.id.pricePerKg);
        saveButton = findViewById(R.id.saveRiceTypeButton);
        viewRiceTypesButton = findViewById(R.id.viewRiceTypesButton);

        saveButton.setOnClickListener(v -> {
            String riceName = riceNameInput.getText().toString().trim();
            String riceType = riceTypeInput.getText().toString().trim();
            String priceText = pricePerKgInput.getText().toString().trim();

            if (riceName.isEmpty() || riceType.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double pricePerKg;
            try {
                pricePerKg = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.addRiceType(riceName, riceType, pricePerKg)) {
                Toast.makeText(this, "Rice type added successfully", Toast.LENGTH_SHORT).show();
                riceNameInput.setText("");
                riceTypeInput.setText("");
                pricePerKgInput.setText("");
            } else {
                Toast.makeText(this, "Failed to add rice type", Toast.LENGTH_SHORT).show();
            }
        });

        viewRiceTypesButton.setOnClickListener(v -> {
            Intent intent = new Intent(RiceTypesActivity.this, RiceTypeListActivity.class);
            startActivity(intent);
        });
    }
}
