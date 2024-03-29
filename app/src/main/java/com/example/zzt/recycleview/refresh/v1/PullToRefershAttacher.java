package com.example.zzt.recycleview.refresh.v1;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.customview.widget.ViewDragHelper;

/**
 * Created by Hale Yang on 2017/8/16.
 * 此乃下拉刷新容器
 */

public class PullToRefershAttacher extends LinearLayout implements NestedScrollingChild {

    private NestedScrollingChildHelper mScrollingChildHelper;
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    //方向
    private int mOrientation = VERTICAL;
    //
    private ViewDragHelper mViewDragHelper;

    public PullToRefershAttacher(@NonNull Context context, @NonNull View view){
        super(context);
        if(view.getLayoutParams() == null){
            view.setLayoutParams(generateDefaultLayoutParams());
        }
        setLayoutParams(view.getLayoutParams());
        ViewParent parent = view.getParent();
        if(parent != null){
            ViewGroup parentG = (ViewGroup) parent;
            parentG.removeView(view);
            parentG.addView(this);
        }
        addView(view);
    }

    private PullToRefershAttacher(Context context) {
        super(context);
    }

    private PullToRefershAttacher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(21)
    private PullToRefershAttacher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    private PullToRefershAttacher(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context, AttributeSet attrs){
        mViewDragHelper = ViewDragHelper.create(this, ViewConfiguration.get(context).getScaledTouchSlop(), new PullCallback());
    }


    public void setOrientation(int orientation){
        if(orientation != HORIZONTAL && orientation != VERTICAL){
            throw  new IllegalArgumentException("the orientation must be HORIZONTAL or VERTICAL");
        }
        this.mOrientation = orientation;
    }

    /**
     * 设置是否可以进行嵌套滚动
     * @param enabled true or false
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        getScrollingChildHelper().setNestedScrollingEnabled(enabled);
    }


    @Override
    public boolean isNestedScrollingEnabled(){
        return getScrollingChildHelper().isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return getScrollingChildHelper().startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        getScrollingChildHelper().stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
    }

    private NestedScrollingChildHelper getScrollingChildHelper() {
        if (mScrollingChildHelper == null) {
            mScrollingChildHelper = new NestedScrollingChildHelper(this);
        }
        return mScrollingChildHelper;
    }


    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * ViewDragHelper callback
     */
    private class PullCallback extends ViewDragHelper.Callback{

        @Override
        public int getViewHorizontalDragRange(View child) {
            return super.getViewHorizontalDragRange(child);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return super.getViewVerticalDragRange(child);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return super.clampViewPositionVertical(child, top, dy);
        }

        @Override
         public boolean tryCaptureView(View child, int pointerId) {
             return false;
         }
     }

}
