package com.zzt.xtrend.pulltorefresh;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.widget.NestedScrollView;


/**
 * @author: zeting
 * @date: 2024/11/3
 * NestedScrollView 刷新
 */
public class PullToRefreshNestedScrollViewV2 extends PullToRefreshBase<NestedScrollView> {
    private static final String TAG = PullToRefreshNestedScrollViewV2.class.getSimpleName();


    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshNestedScrollViewV2(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshNestedScrollViewV2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
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
        initView(context);
    }

    private void initView(Context context) {
    }

    @Override
    protected NestedScrollView createRefreshableView(Context context, AttributeSet attrs) {
        return new NestedScrollView(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 4) {
            View childAt = getChildAt(3);
            if (childAt != null) {
                removeView(childAt);
                NestedScrollView refreshableView = getRefreshableView();
                if (refreshableView != null) {
                    refreshableView.addView(childAt, new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                }
            }
        } else {
            throw new RuntimeException("里面只能允许包含一个视图，请重新查看布局文件");
        }
    }

    /**
     * RecyclerView.canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
     * RecyclerView.canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
     */
    @Override
    protected boolean isReadyForPullDown() {
//        Log.d("PullToRefreshBase-lv2", "向下滑动状态：" + mRefreshableView.canScrollVertically(-1));
        if (mRefreshableView != null) {
            return !mRefreshableView.canScrollVertically(-1);
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullUp() {
        if (mRefreshableView != null) {
            return !mRefreshableView.canScrollVertically(1);
        }
        return false;
    }

    @Override
    protected LoadingLayout createHeaderLoadingLayout(Context context, AttributeSet attrs) {
        return new RotateLoadingLayout(context);
    }
}
