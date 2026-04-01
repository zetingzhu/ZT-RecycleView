package com.example.zzt.recycleview.groupedadapter.sticky;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 吸顶 Header 能力接口。
 * 任何 RecyclerView.Adapter（包括 ListAdapter）只要实现此接口，
 * 就可以配合 RVStickyHeaderLayoutV2 实现吸顶效果。
 */
public interface IStickyHeaderProvider<VH extends RecyclerView.ViewHolder> {
    /**
     * 绑定吸顶 Header 的数据
     */
    void onBindViewHolderStickyHead(VH holder, int headPos, boolean showHead);

    /**
     * 获取当前 position 所属分组的 Header 位置
     */
    int getStickyHeaderPosition(int pos);

    /**
     * 获取下一个分组的 Header 位置
     */
    int getNextStickyHeaderPosition(int currentHeaderPos);
}
