package com.example.zzt.recycleview.smart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.zzt.recycleview.behavior.MyBehavior;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.CoordinatorLayoutListener;
import com.scwang.smart.refresh.layout.listener.MyBehaviorListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * @author: zeting
 * @date: 2024/12/6
 * PullToRefreshBase 转 SmartRefreshLayout 中间试图，切换使用
 * <p>
 * 对应功能文档阅读这个 https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_property.md
 */
public abstract class PullToSmartRefreshLayout<T extends View> extends SmartRefreshLayout {

    /**
     * 子类包含 CoordinatorLayout 并且使用了 MyBehavior ，就必须要实现这个方法，并且返回 true
     * 这个方法可以解决实现 MyBehavior 下拉刷新问题
     */
    public abstract boolean childViewIsCoordinatorLayout();

    /**
     * 找出对于 实现 MyBehavior 的视图
     */
    public abstract View getMyBehaviorView();

    /**
     * 下拉刷新和加载更多的监听器
     */
    private PullToRefreshBase.OnRefreshListener<T> mRefreshListener;


    public PullToSmartRefreshLayout(Context context) {
        super(context);
        initView(context);
    }

    public PullToSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setRefreshHeader(new SmartClassicsHeaderLayout(getContext()));

        if (childViewIsCoordinatorLayout()) {
            setMyBehaviorListener(new MyBehaviorListener() {
                @Override
                public void checkCoordinatorLayout(View content, RefreshKernel kernel, CoordinatorLayoutListener listener) {
                    View myBehaviorView = getMyBehaviorView();
                    if (myBehaviorView != null) {
                        kernel.getRefreshLayout().setEnableNestedScroll(false);
                        //跟单标题滑动到顶部
                        ViewGroup.LayoutParams layoutParams = myBehaviorView.getLayoutParams();
                        if (layoutParams instanceof CoordinatorLayout.LayoutParams && ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior() instanceof MyBehavior) {
                            MyBehavior behavior = (MyBehavior) ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
                            if (behavior != null) {
                                behavior.addOnOffsetChangedListener(new MyBehavior.OnScrollChangeListener() {
                                    @Override
                                    public void onScrollChange(View headerView, View scrollingView, int offsetDy, int leftOffsetDy) {
                                        listener.onCoordinatorUpdate(offsetDy >= 0, leftOffsetDy <= 0);
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }


    /**
     * 设置最后刷新时间
     */
    public void setLastUpdatedLabel() {

    }

    /**
     * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
     *
     * @param smoothScroll 表示是否有平滑滚动，true表示平滑滚动，false表示无平滑滚动
     * @param delayMillis  延迟时间
     */
    public void doPullRefreshing(final boolean smoothScroll, final long delayMillis) {
        autoRefresh();//自动刷新
    }

    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        setEnableRefresh(pullRefreshEnabled);//是否启用下拉刷新功能
    }

    public void setPullLoadEnabled(boolean pullLoadEnabled) {
        setEnableLoadMore(pullLoadEnabled);//是否启用上拉加载功能
    }


    /**
     * 设置刷新监听
     */
    public void setOnRefreshListener(PullToRefreshBase.OnRefreshListener<T> refreshListener) {
        this.mRefreshListener = refreshListener;
        setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {


            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }
        });
    }

    /**
     * 结束下拉刷新
     */
    public void onPullDownRefreshComplete() {
        finishRefresh();
    }

    /**
     * 结束上拉加载更多
     */
    public void onPullUpRefreshComplete() {
        finishLoadMore();
    }
}
