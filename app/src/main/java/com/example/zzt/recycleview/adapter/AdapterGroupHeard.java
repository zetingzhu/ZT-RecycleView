package com.example.zzt.recycleview.adapter;

import android.content.Context;

import com.example.zzt.recycleview.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.example.zzt.recycleview.groupedadapter.holder.BaseViewHolder;

/**
 * @author: zeting
 * @date: 2023/9/1
 */
public class AdapterGroupHeard extends GroupedRecyclerViewAdapter {
    public AdapterGroupHeard(Context context) {
        super(context);
    }

    public AdapterGroupHeard(Context context, boolean useBinding) {
        super(context, useBinding);
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return false;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return 0;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        return 0;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {

    }
}
