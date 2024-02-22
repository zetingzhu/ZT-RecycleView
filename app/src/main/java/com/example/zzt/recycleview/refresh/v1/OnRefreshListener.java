package com.example.zzt.recycleview.refresh.v1;


import android.view.View;

/**
 * 定义了下拉刷新和上拉加载更多的接口。
 *
 * @author Li Hong
 * @since 2013-7-29
 */
public interface OnRefreshListener<V extends View> {

    /**
     * 下拉松手后会被调用
     *
     * @param refreshView 刷新的View
     */
    void onPullDownToRefresh(final PullToRefreshBase<V> refreshView);

    /**
     * 加载更多时会被调用或上拉时调用
     *
     * @param refreshView 刷新的View
     */
    void onPullUpToRefresh(final PullToRefreshBase<V> refreshView);
}
