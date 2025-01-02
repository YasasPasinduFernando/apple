package com.example.harvestflow.ricetypemanagement;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.harvestflow.Database.RiceTypeDatabaseHelper;
import com.example.harvestflow.R;

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

        List<String> riceTypes = dbHelper.getAllRiceTypes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, riceTypes);
        riceTypeListView.setAdapter(adapter);
    }
}
