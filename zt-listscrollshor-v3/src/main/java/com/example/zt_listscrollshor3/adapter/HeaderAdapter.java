package com.example.zt_listscrollshor3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.zt_listscrollshor3.R;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {
    private final List<String> headerList;
    private final List<Integer> columnWidths;

    public HeaderAdapter(List<String> headerList, List<Integer> columnWidths) {
        this.headerList = headerList;
        this.columnWidths = columnWidths;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header_cell, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.tvHeader.setText(headerList.get(position));
        // 设置宽度
        if (columnWidths != null && position < columnWidths.size()) {
            ViewGroup.LayoutParams lp = holder.tvHeader.getLayoutParams();
            lp.width = columnWidths.get(position);
            holder.tvHeader.setLayoutParams(lp);
        }
    }

    @Override
    public int getItemCount() {
        return headerList == null ? 0 : headerList.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }
}
