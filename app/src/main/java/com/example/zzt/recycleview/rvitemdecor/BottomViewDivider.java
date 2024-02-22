package com.example.zzt.recycleview.rvitemdecor;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zzt.recycleview.R;
import com.zzt.utilcode.util.SizeUtils;

/**
 * @author: zeting
 * @date: 2023/11/7
 * 底部视图分割线
 */
public class BottomViewDivider extends RecyclerView.ItemDecoration {
    private static final String TAG = BottomViewDivider.class.getSimpleName();
    // 是否显示底部视图
    boolean isDrawBottomView = false;
    // 绘图最小高度
    int minHeight = 0;

    public BottomViewDivider(boolean isDrawBottomView) {
        this.isDrawBottomView = isDrawBottomView;
        if (isDrawBottomView) {
            minHeight = SizeUtils.dp2px(100);
        }
    }

    public boolean isDrawBottomView() {
        return isDrawBottomView;
    }

    public void setDrawBottomView(boolean drawBottomView) {
        isDrawBottomView = drawBottomView;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        Log.w(TAG, ">>>>>> onDraw");
        if (parent.getLayoutManager() == null || !isDrawBottomView) {
            return;
        }
        drawVertical(c, parent);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int itemCount = 0;
        if (parent.getAdapter() != null) {
            itemCount = parent.getAdapter().getItemCount();
        }
        // 总高度
        int measuredHeight = parent.getMeasuredHeight();
        Log.w(TAG, "RecyclerView height :" + measuredHeight);
        // 所有item 高度和
        int allViewSum = 0;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);
            allViewSum += child.getMeasuredHeight();
            Log.d(TAG, "child i:" + i + " height:" + child.getMeasuredHeight() + " - all height :" + allViewSum + " 》pos：" + pos
                    + " -childCount:" + childCount + " -itemCount:" + itemCount);
            if ((itemCount - 1) == pos) {
                RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
                if (layoutManager != null) {
                    View firstVisiableChildView = layoutManager.findViewByPosition(pos);
                    if (firstVisiableChildView != null) {
                        int itemHeight = firstVisiableChildView.getHeight();
                        int top = firstVisiableChildView.getTop();
                        int bottom = firstVisiableChildView.getBottom();
                        Log.e(TAG, "RecycleView getTop:" + top + " getBottom:" + bottom + " itemHeight:" + itemHeight);
                        if (measuredHeight - bottom > minHeight) {
                            minHeight = measuredHeight - bottom;
                        }
                        int windowWidth = parent.getWidth();
                        // 添加一个布局
                        int saveCount = canvas.save();
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_bottom, null, true);
                        int width = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.AT_MOST);
                        int height = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.AT_MOST);
                        view.measure(width, height);
                        view.layout(0, 0, windowWidth, minHeight);
                        canvas.translate(0, bottom);
                        view.draw(canvas);
                        canvas.restoreToCount(saveCount);
                    }
                }
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        Log.w(TAG, ">>>>>> onDrawOver");
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        Log.w(TAG, ">>>>>> getItemOffsets");
        if (isDrawBottomView) {
            int itemCount = 0;
            if (parent.getAdapter() != null) {
                itemCount = parent.getAdapter().getItemCount();
                // 当前item 下标
                int pos = parent.getChildAdapterPosition(view);
                Log.w(TAG, "getItemOffsets boo：" + ((itemCount - 1) == pos));
                if ((itemCount - 1) == pos) {
                    outRect.set(0, 0, 0, minHeight);
                }
            }
        }
    }
}
