package com.example.zt_listscrollshor3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zt_listscrollshor3.R;

import java.util.Arrays;
import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.RowViewHolder> {
    private final Context context;
    private final List<List<String>> contentList;
    private final List<Integer> columnWidths;
    private final OnRowBindListener onRowBindListener;
    private static final String TAG = "ZTDebug";

    public interface OnRowBindListener {
        void onRowBind(RecyclerView rowRecyclerView);
    }

    public ContentAdapter(Context context, List<List<String>> contentList, List<Integer> columnWidths, OnRowBindListener listener) {
        this.context = context;
        this.contentList = contentList;
        this.columnWidths = columnWidths;
        this.onRowBindListener = listener;
        Log.d(TAG, "ContentAdapter constructed");
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "ContentAdapter onCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.item_content_row, parent, false);
        return new RowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        Log.d(TAG, "ContentAdapter onBindViewHolder: position=" + position);
        List<String> row = contentList.get(position);
        if (holder.recyclerView.getLayoutManager() == null) {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        }
        if (holder.recyclerView.getAdapter() == null) {
            RowAdapter rowAdapter = new RowAdapter(row, columnWidths);
            holder.recyclerView.setAdapter(rowAdapter);
            holder.rowAdapter = rowAdapter;
        } else {
            if (holder.rowAdapter == null) {
                holder.rowAdapter = (RowAdapter) holder.recyclerView.getAdapter();
            }
            // 只刷新数据，不全量刷新
            holder.rowAdapter.setRow(row);
        }
        if (onRowBindListener != null) {
            onRowBindListener.onRowBind(holder.recyclerView);
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    // 修改为 public，便于外部访问
    public static class RowViewHolder extends RecyclerView.ViewHolder {
        public RecyclerView recyclerView;
        public RowAdapter rowAdapter;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rvRow);
        }
    }

}