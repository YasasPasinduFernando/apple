package com.example.harvestflow;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harvestflow.Database.FarmerDatabaseHelper;

public class FarmerListActivity extends AppCompatActivity {
//    private FarmerDatabaseHelper dbHelper;
//    private ListView listView;
//    private int collectorId;
//    private SimpleCursorAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_farmer_list);
//
//        dbHelper = new FarmerDatabaseHelper(this);
//        collectorId = getIntent().getIntExtra("collector_id", -1);
//        listView = findViewById(R.id.farmerListView);
//
//        setupAddButton();
//        loadFarmers();
//    }
//
//    private void setupAddButton() {
//        findViewById(R.id.addFarmerButton).setOnClickListener(v -> {
//            Intent intent = new Intent(this, FarmerManagementActivity.class);
//            intent.putExtra("collector_id", collectorId);
//            startActivityForResult(intent, 1);
//        });
//    }
//
//    private void loadFarmers() {
//        String[] fromColumns = {
//                FarmerDatabaseHelper.FARMER_NAME,
//                FarmerDatabaseHelper.FARMER_NIC,
//                FarmerDatabaseHelper.FARMER_LOCATION,
//                FarmerDatabaseHelper.FARMER_PHONE
//        };
//
//        int[] toViews = {
//                R.id.farmerNameText,
//                R.id.farmerDetailsText
//        };
//
//        Cursor cursor = dbHelper.getReadableDatabase().query(
//                FarmerDatabaseHelper.TABLE_FARMERS,
//                null,
//                FarmerDatabaseHelper.FARMER_COLLECTOR_ID + "=?",
//                new String[]{String.valueOf(collectorId)},
//                null, null,
//                FarmerDatabaseHelper.FARMER_NAME + " ASC"
//        );
//
//        adapter = new SimpleCursorAdapter(
//                this,
//                R.layout.item_farmer,
//                cursor,
//                fromColumns,
//                toViews,
//                0
//        );
//
//        adapter.setViewBinder((view, cursor, columnIndex) -> {
//            if (view.getId() == R.id.farmerDetailsText) {
//                String nic = cursor.getString(cursor.getColumnIndexOrThrow(FarmerDatabaseHelper.FARMER_NIC));
//                String location = cursor.getString(cursor.getColumnIndexOrThrow(FarmerDatabaseHelper.FARMER_LOCATION));
//                String phone = cursor.getString(cursor.getColumnIndexOrThrow(FarmerDatabaseHelper.FARMER_PHONE));
//                ((TextView) view).setText(String.format("NIC: %s\nLocation: %s\nPhone: %s", nic, location, phone));
//                return true;
//            }
//            return false;
//        });
//
//        listView.setAdapter(adapter);
//        setupListViewListeners();
//    }
//
//    private void setupListViewListeners() {
//        listView.setOnItemClickListener((parent, view, position, id) -> {
//            Intent intent = new Intent(this, FarmerManagementActivity.class);
//            intent.putExtra("farmer_id", id);
//            intent.putExtra("collector_id", collectorId);
//            startActivityForResult(intent, 2);
//        });
//
//        listView.setOnItemLongClickListener((parent, view, position, id) -> {
//            showDeleteDialog(id);
//            return true;
//        });
//    }
//
//    private void showDeleteDialog(long farmerId) {
//        new AlertDialog.Builder(this)
//                .setTitle("Delete Farmer")
//                .setMessage("Are you sure you want to delete this farmer?")
//                .setPositiveButton("Delete", (dialog, which) -> {
//                    dbHelper.getWritableDatabase().delete(
//                            FarmerDatabaseHelper.TABLE_FARMERS,
//                            FarmerDatabaseHelper.FARMER_ID + "=?",
//                            new String[]{String.valueOf(farmerId)}
//                    );
//                    loadFarmers();
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            loadFarmers();
//        }
//    }
}
