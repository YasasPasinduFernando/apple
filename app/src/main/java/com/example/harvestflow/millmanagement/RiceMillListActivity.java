package com.example.harvestflow.millmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.harvestflow.Database.RiceMillDatabaseHelper;
import com.example.harvestflow.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.HashMap;
import java.util.List;

public class RiceMillListActivity extends AppCompatActivity implements RiceMillAdapter.OnRiceMillListener {
    private RiceMillDatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private RiceMillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rice_mill_list);

        dbHelper = new RiceMillDatabaseHelper(this);
        setupViews();
        loadRiceMills();
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RiceMillAdapter(dbHelper.getAllRiceMills(), this);
        recyclerView.setAdapter(adapter);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(RiceMillListActivity.this, RiceMillManagementActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRiceMills();
    }

    private void loadRiceMills() {
        List<HashMap<String, String>> riceMills = dbHelper.getAllRiceMills();
        adapter.updateData(riceMills);

        if (riceMills.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditClick(HashMap<String, String> riceMill) {
        // TODO: Implement edit functionality
        // You'll need to create a new activity or dialog for editing
        Toast.makeText(this, "Edit functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(HashMap<String, String> riceMill) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Rice Mill")
                .setMessage("Are you sure you want to delete " + riceMill.get("name") + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteRiceMill(riceMill.get("id"))) {
                        Toast.makeText(this, "Rice mill deleted successfully", Toast.LENGTH_SHORT).show();
                        loadRiceMills();
                    } else {
                        Toast.makeText(this, "Failed to delete rice mill", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}