package com.example.zzt.recycleview.refresh.v1;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

/**
 * @author: zeting
 * @date: 2024/11/3
 * NestedScrollView 刷新
 */
public class PullToRefreshNestedScrollViewV2 extends PullToRefreshBase<NestedScrollView> {
    OnScrollChangeListener onScrollChangeListener;

    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshNestedScrollViewV2(Context context) {
        // this(context, null);
        super(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshNestedScrollViewV2(Context context, AttributeSet attrs) {
        // this(context, attrs, 0);
        super(context, attrs);
    }

    /**
     * 构造方法
     *
     * @param context  context
     * @param attrs    attrs
     * @param defStyle defStyle
     */
    public PullToRefreshNestedScrollViewV2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected NestedScrollView createRefreshableView(Context context, AttributeSet attrs) {
        NestedScrollViewB nestedScrollViewB = new NestedScrollViewB(context, attrs);
        nestedScrollViewB.setOnScrollChangeListener(onScrollChangeListener);
        return nestedScrollViewB;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() >= 4) {
            View childAt = getChildAt(3);
            if (childAt != null) {
                removeView(childAt);
                NestedScrollView refreshableView = getRefreshableView();
                if (refreshableView != null) {
                    refreshableView.addView(childAt, new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                }
            }
        }
    }

    @Override
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        onScrollChangeListener = l;
        if (getChildCount() >= 3) {
            View childAtF = getChildAt(1);
            if (childAtF instanceof FrameLayout) {
                View childAtSv = ((FrameLayout) childAtF).getChildAt(0);
                if (childAtSv instanceof NestedScrollViewB) {
                    childAtSv.setOnScrollChangeListener(onScrollChangeListener);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View childAt1 = getChildAt(1);

        childAt1.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("Log", "ScrollChangeListener onLayout childAt1 onScrollChange:" + scrollY);
            }
        });

        Log.d("Log", "ScrollChangeListener onLayout childAt1:" + childAt1);
    }

    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullUp() {
        View scrollViewChild = mRefreshableView.getChildAt(0);
        if (null != scrollViewChild) {
            return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
        }
        return false;
    }

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new HeaderLoadingLayout(context);
    }


    @Override
    public void setOnRefreshListenerV2(OnRefreshListenerV2<NestedScrollView> refreshListener) {

    }
}
