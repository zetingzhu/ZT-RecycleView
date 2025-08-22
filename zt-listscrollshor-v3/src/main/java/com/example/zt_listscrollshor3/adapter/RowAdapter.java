package com.example.zt_listscrollshor3.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zt_listscrollshor3.R;

import java.util.List;

public class RowAdapter extends RecyclerView.Adapter<RowAdapter.CellViewHolder> {
    private List<String> row;
    private final List<Integer> columnWidths;

    public RowAdapter(List<String> row, List<Integer> columnWidths) {
        this.row = row;
        this.columnWidths = columnWidths;
        Log.d("ZTDebug", "RowAdapter constructed for row hash=" + row.hashCode());
    }

    // 只刷新被修改的列，使用更高效的更新方式
    public void setRow(List<String> newRow, List<Integer> changedColumns) {
        this.row = newRow;
        Log.d("ZTDebug", "RowAdapter setRow: row hash=" + newRow.hashCode() + ", changedColumns=" + changedColumns);
        if (changedColumns != null && !changedColumns.isEmpty()) {
            // 只更新变化的列，减少不必要的刷新
            for (int col : changedColumns) {
                if (col >= 0 && col < row.size()) {
                    notifyItemChanged(col);
                }
            }
        } else {
            // 使用范围更新而不是全量更新
            notifyItemRangeChanged(0, row.size());
        }
    }

    // 保留原有 setRow 兼容老调用
    public void setRow(List<String> row) {
        setRow(row, null);
    }

    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("ZTDebug", "RowAdapter onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content_cell, parent, false);
        return new CellViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
        Log.d("ZTDebug", "RowAdapter onBindViewHolder: cell position=" + position);
        holder.textView.setText(row.get(position));
        // 设置宽度
        if (columnWidths != null && position < columnWidths.size()) {
            ViewGroup.LayoutParams lp = holder.textView.getLayoutParams();
            lp.width = columnWidths.get(position);
            holder.textView.setLayoutParams(lp);
        }
    }

    @Override
    public int getItemCount() {
        return row.size();
    }

    public static class CellViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CellViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvCell);
        }
    }
}
