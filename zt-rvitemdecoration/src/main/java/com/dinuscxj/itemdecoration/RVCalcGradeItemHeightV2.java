package com.dinuscxj.itemdecoration;

import android.graphics.Rect;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: zeting
 * @date: 2025/8/15
 * 统一计算 Grid 一行高度，取最大值计算，不能成功
 */
public class RVCalcGradeItemHeightV2 extends RecyclerView.ItemDecoration {
    private final SparseIntArray rowHeightCache = new SparseIntArray();
    private boolean enableCache = true;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (layoutManager == null) return;

        int spanCount = layoutManager.getSpanCount();
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) return;

        int row = position / spanCount;

        // 如果已经缓存过这行的高度，直接使用
        if (enableCache && rowHeightCache.get(row, -1) != -1) {
            setViewHeight(view, rowHeightCache.get(row));
            return;
        }

        // 如果是行首项目，计算整行高度
        if (position % spanCount == 0) {
            calculateAndSetRowHeight(parent, layoutManager, spanCount, row);
        }
    }

    private void calculateAndSetRowHeight(RecyclerView parent, GridLayoutManager layoutManager,
                                          int spanCount, int row) {
        int rowStart = row * spanCount;
        int rowEnd = Math.min(rowStart + spanCount, parent.getAdapter().getItemCount()) - 1;

        // 方案1：测量当前可见的item
        int maxHeight = 0;
        for (int i = rowStart; i <= rowEnd; i++) {
            View child = layoutManager.findViewByPosition(i);
            if (child != null) {
                measureView(child);
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }

        // 方案2：如果可见item不够，使用预估高度（需要Adapter配合）
        if (maxHeight == 0 && parent.getAdapter() instanceof HeightEstimator) {
            maxHeight = ((HeightEstimator) parent.getAdapter()).estimateRowHeight(row, spanCount);
        }

        if (maxHeight > 0) {
            rowHeightCache.put(row, maxHeight);
            // 只设置当前可见的item
            for (int i = rowStart; i <= rowEnd; i++) {
                View child = layoutManager.findViewByPosition(i);
                if (child != null) {
                    setViewHeight(child, maxHeight);
                }
            }
        }
    }

    private void measureView(View view) {
        if (View.MeasureSpec.getMode(view.getWidth()) == View.MeasureSpec.UNSPECIFIED) {
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
        }
    }

    private void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.height != height) {
            params.height = height;
            view.setLayoutParams(params);
        }
    }

    public void clearCache() {
        rowHeightCache.clear();
    }

    public void setEnableCache(boolean enable) {
        this.enableCache = enable;
    }

    // Adapter可以实现此接口提供预估高度
    public interface HeightEstimator {
        int estimateRowHeight(int row, int spanCount);
    }
}