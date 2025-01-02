package com.example.harvestflow.ricetypemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.harvestflow.Database.RiceTypeDatabaseHelper;
import com.example.harvestflow.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiceTypeListActivity extends AppCompatActivity {
    private RiceTypeDatabaseHelper dbHelper;
    private ListView riceTypeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rice_type_list);

        riceTypeListView = findViewById(R.id.riceTypeListView);
        dbHelper = new RiceTypeDatabaseHelper(this);

        List<HashMap<String, String>> riceTypes = dbHelper.getAllRiceTypes();
        RiceTypeAdapter adapter = new RiceTypeAdapter(riceTypes);
        riceTypeListView.setAdapter(adapter);
    }

    private class RiceTypeAdapter extends ArrayAdapter<HashMap<String, String>> {
        public RiceTypeAdapter(List<HashMap<String, String>> riceTypes) {
            super(RiceTypeListActivity.this, R.layout.rice_type_list_item, riceTypes);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.rice_type_list_item, parent, false);
            }

            HashMap<String, String> riceType = getItem(position);

            TextView nameTextView = convertView.findViewById(R.id.riceNameTextView);
            TextView typeTextView = convertView.findViewById(R.id.riceTypeTextView);
            TextView priceTextView = convertView.findViewById(R.id.ricePriceTextView);

            if (riceType != null) {
                nameTextView.setText(riceType.get("name"));
                typeTextView.setText(riceType.get("type"));
                priceTextView.setText("Rs. " + riceType.get("price_per_kg") + "/kg");
            }

            return convertView;
        }
    }
}