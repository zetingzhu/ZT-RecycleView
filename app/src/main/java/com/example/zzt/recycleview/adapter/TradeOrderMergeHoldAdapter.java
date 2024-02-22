package com.example.zzt.recycleview.adapter;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: zeting
 * @date: 2024/1/26
 * RecycleView 固定吸顶适配器
 */
public abstract class TradeOrderMergeHoldAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    /**
     * 显示滚动头部信息
     *
     * @param holder
     * @param headPos
     * @param showHead
     */
    public abstract void onBindViewHolderHead(VH holder, int headPos, boolean showHead);

    /**
     * 判断当前头部id是否固定
     *
     * @param pos
     * @return
     */
    public abstract boolean isStickyHeaderBoo(  int pos);
}
