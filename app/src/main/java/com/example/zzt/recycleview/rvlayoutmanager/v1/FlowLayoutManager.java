package com.example.zzt.recycleview.rvlayoutmanager.v1;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2017/7/3.
 */

public class FlowLayoutManager extends RecyclerView.LayoutManager {

    private int width, height;
    private int left, top, right;
    //最大容器的宽度
    private int usedMaxWidth;
    //竖直方向上的偏移量
    private int verticalScrollOffset = 0;
    //计算显示的内容的高度
    private int totalHeight = 0;
    //当前行第一个View的下标
    private int rowFirstPos = 0;
    private Row row = new Row();
    private boolean notify;

    public FlowLayoutManager(Context context) {
        this.context = context;
        setMeasurementCacheEnabled(false);
    }

    private Context context;

    public class Item {
        int useHeight;
        View view;

        public Item(int useHeight, View view) {
            this.useHeight = useHeight;
            this.view = view;

        }
    }

    public class Row {
        public void setCuTop(float cuTop) {
            this.cuTop = cuTop;
        }

        public void setMaxHeight(float maxHeight) {
            this.maxHeight = maxHeight;
        }

        float cuTop;
        float maxHeight;

        List<Item> views = new ArrayList<>();

        public void addViews(Item view) {
            views.add(view);
        }

        public void clear() {
            cuTop = 0;
            maxHeight = 0;
            views.clear();
        }
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        width = getWidth();
        height = getHeight();
        totalHeight = 0;
        left = getPaddingLeft();
        right = getPaddingRight();
        top = getPaddingTop();
        usedMaxWidth = width - left - right;
        int cuLineTop = top;
        //当前行使用的宽度
        int cuLineWidth = 0;
        int itemLeft;
        int itemTop;
        int maxHeightItem = 0;
        row.clear();
        rowFirstPos = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View childAt = recycler.getViewForPosition(i);
            addView(childAt);
            if (View.GONE == childAt.getVisibility()) {
                continue;
            }
            measureChildWithMargins(childAt, 0, 0);
            int childWidth = getDecoratedMeasuredWidth(childAt);
            int childHeight = getDecoratedMeasuredHeight(childAt);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childAt.getLayoutParams();
            int rightMargin = params.rightMargin;
            int leftMargin = params.leftMargin;
            int bottomMargin = params.bottomMargin;
            int topMargin = params.topMargin;
            int childUseWidth = childWidth + leftMargin + rightMargin;
            int childUseHeight = childHeight + topMargin + bottomMargin;
            //如果加上当前的item还小于最大的宽度的话
            if (cuLineWidth + childUseWidth <= usedMaxWidth) {
                itemLeft = left + cuLineWidth + leftMargin;
                itemTop = cuLineTop + topMargin;
                layoutDecorated(childAt, itemLeft, itemTop, itemLeft + childWidth, itemTop + childHeight);
                cuLineWidth += childUseWidth;
                maxHeightItem = Math.max(maxHeightItem, childUseHeight);
                row.addViews(new Item(childUseHeight, childAt));
                row.setCuTop(cuLineTop);
                row.setMaxHeight(maxHeightItem);
            } else {
                int remain = usedMaxWidth - cuLineWidth;
                int avg = remain / (i - rowFirstPos);
                int offset = 0;
                for (int j = rowFirstPos; j < i; j++) {
                    View v = findViewByPosition(j);
                    if (v != null) {
                        v.measure(View.MeasureSpec.makeMeasureSpec(v.getMeasuredWidth() + avg, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(v.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
                        v.layout(v.getLeft() + offset, v.getTop(), v.getLeft() + offset + v.getMeasuredWidth(), v.getBottom());
                        offset += avg;
                    }
                }
                //换行
                rowFirstPos = i;
                formatAboveRow();
                cuLineTop += maxHeightItem;
                totalHeight += maxHeightItem;
                itemTop = cuLineTop + topMargin;
                itemLeft = left + leftMargin;
                layoutDecorated(childAt, itemLeft, itemTop, itemLeft + childWidth, itemTop + childHeight);
                cuLineWidth = childUseWidth;
                maxHeightItem = childUseHeight;
                row.addViews(new Item(childUseHeight, childAt));
                row.setCuTop(cuLineTop);
                row.setMaxHeight(maxHeightItem);
            }
            //不要忘了最后一行进行刷新下布局
            if (i == getItemCount() - 1) {
                formatAboveRow();
                totalHeight += maxHeightItem;
            }

        }
        totalHeight = Math.max(totalHeight, getVerticalSpace());
    }

    /**
     * 计算每一行没有居中的viewgroup，让居中显示
     */
    private void formatAboveRow() {
        List<Item> views = row.views;
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i).view;
            if (getDecoratedTop(view) < row.cuTop + (row.maxHeight - views.get(i).useHeight) / 2) {
                layoutDecoratedWithMargins(view, getDecoratedLeft(view),
                        (int) (row.cuTop + (row.maxHeight - views.get(i).useHeight) / 2), getDecoratedRight(view),
                        (int) (row.cuTop + (row.maxHeight - views.get(i).useHeight) / 2 + getDecoratedMeasuredHeight(view)));
            }
        }
        row.clear();
    }

    /**
     * 竖直方向需要滑动的条件
     *
     * @return
     */
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    //监听竖直方向滑动的偏移量
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler,
                                  RecyclerView.State state) {

        //实际要滑动的距离
        int travel = dy;

        //如果滑动到最顶部
        if (verticalScrollOffset + dy < 0) {//限制滑动到顶部之后，不让继续向上滑动了
            travel = -verticalScrollOffset;//verticalScrollOffset=0
        } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部
            travel = totalHeight - getVerticalSpace() - verticalScrollOffset;//verticalScrollOffset=totalHeight - getVerticalSpace()
        }

        //将竖直方向的偏移量+travel
        verticalScrollOffset += travel;

        // 平移容器内的item
        offsetChildrenVertical(-travel);

        return travel;
    }

    private int getVerticalSpace() {
        return height - getPaddingBottom() - getPaddingTop();
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int measureWidth = View.MeasureSpec.getSize(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int measureHeight = View.MeasureSpec.getSize(heightSpec);
        Log.e("FlowLayoutManager","measureWidth=="+measureWidth);
        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = measureWidth;
        } else {
            //以实际屏宽为标准
            width = context.getResources().getDisplayMetrics().widthPixels;
        }
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            measureHeight(recycler);
            height = totalHeight;
        }
        setMeasuredDimension(width, height);
    }

    private void measureHeight(RecyclerView.Recycler recycler) {
        if (!notify)
            return;
        detachAndScrapAttachedViews(recycler);
        totalHeight = 0;
        left = getPaddingLeft();
        right = getPaddingRight();
        top = getPaddingTop();
        usedMaxWidth = width - left - right;
        int cuLineTop = top;
        //当前行使用的宽度
        int cuLineWidth = 0;
        int maxHeightItem = 0;
        row.clear();
        for (int i = 0; i < getItemCount(); i++) {
            View childAt = recycler.getViewForPosition(i);
            addView(childAt);
            if (View.GONE == childAt.getVisibility()) {
                continue;
            }
            measureChildWithMargins(childAt, 0, 0);
            int childWidth = getDecoratedMeasuredWidth(childAt);
            int childHeight = getDecoratedMeasuredHeight(childAt);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childAt.getLayoutParams();
            int rightMargin = params.rightMargin;
            int leftMargin = params.leftMargin;
            int bottomMargin = params.bottomMargin;
            int topMargin = params.topMargin;
            int childUseWidth = childWidth + leftMargin + rightMargin;
            int childUseHeight = childHeight + topMargin + bottomMargin;
            //如果加上当前的item还小于最大的宽度的话
            if (cuLineWidth + childUseWidth <= usedMaxWidth) {
                cuLineWidth += childUseWidth;
                maxHeightItem = Math.max(maxHeightItem, childUseHeight);
                row.addViews(new Item(childUseHeight, childAt));
                row.setCuTop(cuLineTop);
                row.setMaxHeight(maxHeightItem);
            } else {
                //换行
                formatAboveRow();
                cuLineTop += maxHeightItem;
                totalHeight += maxHeightItem;
                cuLineWidth = childUseWidth;
                maxHeightItem = childUseHeight;
                row.addViews(new Item(childUseHeight, childAt));
                row.setCuTop(cuLineTop);
                row.setMaxHeight(maxHeightItem);
            }
            //不要忘了最后一行进行刷新下布局
            if (i == getItemCount() - 1) {
                formatAboveRow();
                totalHeight += maxHeightItem;
            }
        }
        totalHeight += getPaddingTop() + getPaddingBottom();
        totalHeight = Math.max(totalHeight, getVerticalSpace());
        notify = false;
    }

    public void notifyDateSetChanged() {
        notify = true;
    }
}
