package com.example.harvestflow.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.harvestflow.R;
import java.util.HashMap;
import java.util.List;
/*This is for connector as databse
and my view approved rice quantities to mills*/
public class ApprovedQuantitiesAdapter extends RecyclerView.Adapter<ApprovedQuantitiesAdapter.ViewHolder> {
    private List<HashMap<String, String>> approvedQuantities;

    public ApprovedQuantitiesAdapter(List<HashMap<String, String>> approvedQuantities) {
        this.approvedQuantities = approvedQuantities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_approved_quantity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> item = approvedQuantities.get(position);

        holder.millNameTv.setText(item.get("mill_name"));
        holder.riceTypeTv.setText("Rice Type: " + item.get("rice_type"));
        holder.quantityTv.setText("Quantity: " + item.get("quantity") + " kg");
        holder.timestampTv.setText(item.get("timestamp"));
    }

    @Override
    public int getItemCount() {
        return approvedQuantities != null ? approvedQuantities.size() : 0;
    }

    public void updateData(List<HashMap<String, String>> newData) {
        this.approvedQuantities = newData;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView millNameTv;
        TextView riceTypeTv;
        TextView quantityTv;
        TextView timestampTv;

        ViewHolder(View view) {
            super(view);
            millNameTv = view.findViewById(R.id.tv_mill_name);
            riceTypeTv = view.findViewById(R.id.tv_rice_type);
            quantityTv = view.findViewById(R.id.tv_quantity);
            timestampTv = view.findViewById(R.id.tv_timestamp);
        }
    }
}