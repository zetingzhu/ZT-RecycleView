package com.example.zt_listscrollshor3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zt_listscrollshor3.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FixedColumnAdapter extends RecyclerView.Adapter<FixedColumnAdapter.FixedColumnViewHolder> {
    private List<String> fixedColumnList;

    public FixedColumnAdapter(List<String> fixedColumnList) {
        this.fixedColumnList = fixedColumnList;
    }

    @NonNull
    @Override
    public FixedColumnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fixed_column_cell, parent, false);
        return new FixedColumnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixedColumnViewHolder holder, int position) {
        holder.tvFixedColumn.setText(fixedColumnList.get(position));
    }

    @Override
    public int getItemCount() {
        return fixedColumnList == null ? 0 : fixedColumnList.size();
    }

    static class FixedColumnViewHolder extends RecyclerView.ViewHolder {
        TextView tvFixedColumn;

        FixedColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFixedColumn = itemView.findViewById(R.id.tvFixedColumn);
        }
    }
}
