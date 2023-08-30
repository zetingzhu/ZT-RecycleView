package com.example.zzt.recycleview.decor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.zzt.utilcode.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zeting
 * @date: 2021/10/22
 * 多几个参数，可以添加隐藏某一条的上面分割线
 * VerticalTopBottomSpaceDivider 进化版本
 */
public class RecycleViewDecorationRemovePos extends RecyclerView.ItemDecoration {
    private static final String TAG = RecycleViewDecorationRemovePos.class.getSimpleName();
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    // 垂直上下都有线
    public static final int VERTICAL_TOB_BOTTOM = 2;
    // 添加一个，没有最后一条线
    public static final int VERTICAL_NOT_BOTTOM = 3;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private Drawable mDivider;
    private final Rect mBounds = new Rect();
    private int mOrientation;

    private List<Integer> hidePositionList;

    public RecycleViewDecorationRemovePos(Context context, int orientation) {
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

    /**
     * 设置那些下面需要隐藏分割线
     *
     * @param list
     */
    public void setHideBottom(List<Integer> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            hidePositionList = new ArrayList<>();
            hidePositionList.addAll(list);
            Log.d(TAG, "设置隐藏的是那些数据：" + hidePositionList);
        }
    }

    public void setOrientation(int orientation) {
        if (orientation != VERTICAL_TOB_BOTTOM && orientation != VERTICAL && orientation != VERTICAL_NOT_BOTTOM) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
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

        // 顶部
        int firstVisiblePosition = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        Log.d(TAG, "firstVisiblePosition:" + firstVisiblePosition);

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            // 画顶部第一条线
            if (mOrientation == VERTICAL_TOB_BOTTOM) {
                if ((firstVisiblePosition + i) == 0) {
                    final int top = mBounds.top + Math.round(child.getTranslationY());
                    final int bottom = top + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            }
            if (mOrientation == VERTICAL_NOT_BOTTOM && i == (childCount - 1)) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(hidePositionList) && hidePositionList.contains(firstVisiblePosition + i)) {
                final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                mDivider.setBounds(left, bottom, right, bottom);
                mDivider.draw(canvas);
            } else {
                final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                final int top = bottom - mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
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
        if (mOrientation == VERTICAL_TOB_BOTTOM) {
            if (position == 0) {
                outRect.set(0, mDivider.getIntrinsicHeight(), 0, mDivider.getIntrinsicHeight());
            } else {
                if (CollectionUtils.isNotEmpty(hidePositionList)) {
                    boolean contains = hidePositionList.contains(position);
                    if (contains) {
                        outRect.set(0, 0, 0, 0);
                    } else {
                        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                    }
                } else {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                }
            }
        } else {
            if (mOrientation == VERTICAL_NOT_BOTTOM && itemCount > 0 && position == (itemCount - 1)) {
                outRect.set(0, 0, 0, 0);
            } else if (CollectionUtils.isNotEmpty(hidePositionList)) {
                boolean contains = hidePositionList.contains(position);
                if (contains) {
                    outRect.set(0, 0, 0, 0);
                } else {
                    outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
                }
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        }
    }
}
