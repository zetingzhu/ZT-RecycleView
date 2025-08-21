package com.dinuscxj.itemdecoration;

import android.graphics.Rect;
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
public class RVCalcGradeItemHeight extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            if (layoutManager == null) return;

            int spanCount = layoutManager.getSpanCount();
            int position = parent.getChildAdapterPosition(view);

            // 如果是行首项目，统一整行高度
            if (position % spanCount == 0) {
                int rowStart = position;
                int rowEnd = Math.min(rowStart + spanCount, state.getItemCount()) - 1;

                // 1. 找出行中最高项目的高度
                int maxHeight = 0;
                for (int i = rowStart; i <= rowEnd; i++) {
                    View child = layoutManager.findViewByPosition(i);
                    if (child != null) {
                        child.measure(
                                View.MeasureSpec.makeMeasureSpec(child.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        );
                        maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
                    }
                }

                // 2. 统一设置行高
                if (maxHeight > 0) {
                    for (int i = rowStart; i <= rowEnd; i++) {
                        View child = layoutManager.findViewByPosition(i);
                        if (child != null) {
                            ViewGroup.LayoutParams params = child.getLayoutParams();
                            params.height = maxHeight;
                            child.setLayoutParams(params);
                        }
                    }
                }
            }
        }
    }
}