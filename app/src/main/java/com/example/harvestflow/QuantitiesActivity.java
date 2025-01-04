package com.example.harvestflow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harvestflow.Database.FarmerDatabaseHelper;
import com.example.harvestflow.Database.QuantityHelperDatabase;
import com.example.harvestflow.Database.RiceTypeDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuantitiesActivity extends Activity {
    private Spinner farmerSpinner, riceTypeSpinner;
    private EditText totalWeightInput, bagWeightInput, moistureWeightInput, otherDeductionsInput;
    private TextView netWeightDisplay;
    private Button saveButton;
    private FarmerDatabaseHelper farmerDbHelper;
    private RiceTypeDatabaseHelper riceTypeDbHelper;
    private QuantityHelperDatabase quantityDbHelper;

    private List<HashMap<String, String>> farmerList;
    private List<HashMap<String, String>> riceTypeList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quantity);

        farmerSpinner = findViewById(R.id.farmer_spinner);
        riceTypeSpinner = findViewById(R.id.rice_type_spinner);
        totalWeightInput = findViewById(R.id.total_weight_input);
        bagWeightInput = findViewById(R.id.bag_weight_input);
        moistureWeightInput = findViewById(R.id.moisture_weight_input);
        otherDeductionsInput = findViewById(R.id.other_deductions_input);
        netWeightDisplay = findViewById(R.id.net_weight_display);
        saveButton = findViewById(R.id.save_button);

        farmerDbHelper = new FarmerDatabaseHelper(this);
        riceTypeDbHelper = new RiceTypeDatabaseHelper(this);
        quantityDbHelper = new QuantityHelperDatabase(this);

        loadFarmersIntoSpinner();
        loadRiceTypesIntoSpinner();
        setupBackButton();
        setupWeightCalculation();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuantityData();
            }
        });
    }

    private void loadFarmersIntoSpinner() {
        farmerList = farmerDbHelper.getAllFarmers();
        List<String> farmerNames = new ArrayList<>();

        for (HashMap<String, String> farmer : farmerList) {
            farmerNames.add(farmer.get("name"));
        }

        ArrayAdapter<String> farmerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, farmerNames);
        farmerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        farmerSpinner.setAdapter(farmerAdapter);
    }

    private void loadRiceTypesIntoSpinner() {
        riceTypeList = riceTypeDbHelper.getAllRiceTypes();
        List<String> riceTypeNames = new ArrayList<>();

        for (HashMap<String, String> riceType : riceTypeList) {
            riceTypeNames.add(riceType.get("name"));
        }

        ArrayAdapter<String> riceTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, riceTypeNames);
        riceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riceTypeSpinner.setAdapter(riceTypeAdapter);
    }

    private void setupWeightCalculation() {
        TextWatcher weightWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateNetWeight();
            }
        };

        totalWeightInput.addTextChangedListener(weightWatcher);
        bagWeightInput.addTextChangedListener(weightWatcher);
        moistureWeightInput.addTextChangedListener(weightWatcher);
        otherDeductionsInput.addTextChangedListener(weightWatcher);
    }

    private void calculateNetWeight() {
        try {
            double totalWeight = getDoubleFromEditText(totalWeightInput);
            double bagWeight = getDoubleFromEditText(bagWeightInput);
            double moistureWeight = getDoubleFromEditText(moistureWeightInput);
            double otherDeductions = getDoubleFromEditText(otherDeductionsInput);

            double netWeight = totalWeight - (bagWeight + moistureWeight + otherDeductions);
            netWeightDisplay.setText(String.format("Net Weight: %.2f kg", netWeight));
        } catch (NumberFormatException e) {
            netWeightDisplay.setText("Net Weight: 0.00 kg");
        }
    }

    private double getDoubleFromEditText(EditText editText) {
        String text = editText.getText().toString();
        return text.isEmpty() ? 0.0 : Double.parseDouble(text);
    }

    private void saveQuantityData() {
        int selectedFarmerIndex = farmerSpinner.getSelectedItemPosition();
        int selectedRiceTypeIndex = riceTypeSpinner.getSelectedItemPosition();

        if (selectedFarmerIndex < 0 || selectedRiceTypeIndex < 0) {
            Toast.makeText(this, "Please select both Farmer and Rice Type", Toast.LENGTH_SHORT).show();
            return;
        }

        int farmerId = Integer.parseInt(farmerList.get(selectedFarmerIndex).get("id"));
        int riceTypeId = Integer.parseInt(riceTypeList.get(selectedRiceTypeIndex).get("id"));

        try {
            double totalWeight = Double.parseDouble(totalWeightInput.getText().toString());
            double bagWeight = Double.parseDouble(bagWeightInput.getText().toString());
            double moistureWeight = Double.parseDouble(moistureWeightInput.getText().toString());
            double otherDeductions = Double.parseDouble(otherDeductionsInput.getText().toString());

            boolean success = quantityDbHelper.addQuantity(farmerId, riceTypeId, totalWeight,
                    bagWeight, moistureWeight, otherDeductions);

            if (success) {
                Toast.makeText(this, "Quantity data added successfully", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(this, "Failed to add quantity data", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for weights and deductions",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        totalWeightInput.setText("");
        bagWeightInput.setText("");
        moistureWeightInput.setText("");
        otherDeductionsInput.setText("");
        netWeightDisplay.setText("Net Weight: 0.00 kg");
        farmerSpinner.setSelection(0);
        riceTypeSpinner.setSelection(0);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}