package com.example.zzt.recycleview.rvitemdecor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: zeting
 * @date: 2021/2/1
 * 垂直画上下都显示分割线（包括第一条开始和最后一条结束）
 */
public class VerticalTopBottomSpaceDivider extends RecyclerView.ItemDecoration {
    private static final String TAG = VerticalTopBottomSpaceDivider.class.getSimpleName();
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    // 垂直上下都有线
    public static final int VERTICAL_TOP_BOTTOM = 2;
    // 添加一个，没有最后一条线
    public static final int VERTICAL_NOT_BOTTOM = 3;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private Drawable mDivider;
    private final Rect mBounds = new Rect();
    private int mOrientation;
    // 隐藏某一天下面线
    private boolean hideBottom = false;
    private int hidePosition;

    public VerticalTopBottomSpaceDivider(Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(orientation);
    }

    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        mDivider = drawable;
    }

    // 支持动态修改未登录的时候隐藏下面的分割线
    public void setHideBottom(boolean setHideBottom, int setHidePosition) {
        this.hideBottom = setHideBottom;
        this.hidePosition = setHidePosition;
    }

    public void setOrientation(int orientation) {
        if (orientation != VERTICAL_TOP_BOTTOM && orientation != VERTICAL && orientation != VERTICAL_NOT_BOTTOM) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getLayoutManager() == null || mDivider == null) {
            return;
        }
        drawVertical(c, parent);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            // 画顶部第一条线
            if (mOrientation == VERTICAL_TOP_BOTTOM) {
                if (i == 0) {
                    final int top = mBounds.top + Math.round(child.getTranslationY());
                    final int bottom = top + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            }
            if (mOrientation == VERTICAL_NOT_BOTTOM && i == (childCount - 1)) {
                continue;
            }
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        // 总数
        int itemCount = 0;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager != null) {
            itemCount = layoutManager.getItemCount();
        }
        int position = parent.getChildAdapterPosition(view);
        if (mOrientation == VERTICAL_TOP_BOTTOM) {
            if (position == 0) {
                outRect.set(0, mDivider.getIntrinsicHeight(), 0, mDivider.getIntrinsicHeight());
            } else {
                if (hideBottom && position == hidePosition) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                }
            }
        } else {
            if (mOrientation == VERTICAL_NOT_BOTTOM && itemCount > 0 && position == (itemCount - 1)) {
                outRect.set(0, 0, 0, 0);
            } else if (hideBottom && position == hidePosition) {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        }
    }
}
