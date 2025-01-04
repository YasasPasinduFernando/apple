package com.example.harvestflow.millmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.harvestflow.R;
import com.google.android.material.button.MaterialButton;
import java.util.HashMap;
import java.util.List;

public class RiceMillAdapter extends RecyclerView.Adapter<RiceMillAdapter.RiceMillViewHolder> {
    private List<HashMap<String, String>> riceMills;
    private final OnRiceMillListener listener;

    public interface OnRiceMillListener {
        void onEditClick(HashMap<String, String> riceMill);
        void onDeleteClick(HashMap<String, String> riceMill);
    }

    public RiceMillAdapter(List<HashMap<String, String>> riceMills, OnRiceMillListener listener) {
        this.riceMills = riceMills;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RiceMillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rice_mill, parent, false);
        return new RiceMillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RiceMillViewHolder holder, int position) {
        HashMap<String, String> riceMill = riceMills.get(position);
        holder.bind(riceMill);
    }

    @Override
    public int getItemCount() {
        return riceMills.size();
    }

    public void updateData(List<HashMap<String, String>> newRiceMills) {
        this.riceMills = newRiceMills;
        notifyDataSetChanged();
    }

    class RiceMillViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMillName;
        private final TextView tvLocation;
        private final TextView tvContact;
        private final TextView tvRegId;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        RiceMillViewHolder(View itemView) {
            super(itemView);
            tvMillName = itemView.findViewById(R.id.tvMillName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvContact = itemView.findViewById(R.id.tvContact);
            tvRegId = itemView.findViewById(R.id.tvRegId);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(HashMap<String, String> riceMill) {
            tvMillName.setText(riceMill.get("name"));
            tvLocation.setText(riceMill.get("location"));
            tvContact.setText(riceMill.get("contact"));
            tvRegId.setText("Reg ID: " + riceMill.get("regId"));

            btnEdit.setOnClickListener(v -> listener.onEditClick(riceMill));
            btnDelete.setOnClickListener(v -> listener.onDeleteClick(riceMill));
        }
    }
}