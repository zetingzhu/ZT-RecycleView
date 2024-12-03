package com.example.zzt.recycleview.refresh.v1;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.example.zzt.recycleview.R;

/**
 * @author: zeting
 * @date: 2024/11/3
 * NestedScrollView 刷新
 */
public abstract class PullToRefreshNestedScrollViewV3 extends PullToRefreshBase<NestedScrollView> {
    NestedScrollView nestedScrollView;
    /**
     * 构造方法
     *
     * @param context context
     */
    public PullToRefreshNestedScrollViewV3(Context context) {
        // this(context, null);
        super(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public PullToRefreshNestedScrollViewV3(Context context, AttributeSet attrs) {
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
    public PullToRefreshNestedScrollViewV3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected NestedScrollView createRefreshableView(Context context, AttributeSet attrs) {
        View refreshableView = createRefreshableView();
        NestedScrollView viewById = refreshableView.findViewById(R.id.pull_layout);
        return viewById;
    }

    public abstract View createRefreshableView();


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
