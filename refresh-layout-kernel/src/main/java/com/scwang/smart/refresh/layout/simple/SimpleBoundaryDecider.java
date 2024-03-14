package com.scwang.smart.refresh.layout.simple;

import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import com.scwang.smart.refresh.layout.listener.ScrollBoundaryDecider;
import com.scwang.smart.refresh.layout.util.SmartUtil;

/**
 * 滚动边界
 * Created by scwang on 2017/7/8.
 */
public class SimpleBoundaryDecider implements ScrollBoundaryDecider {

    //<editor-fold desc="Internal">
    public PointF mActionEvent;
    public ScrollBoundaryDecider boundary;
    public boolean mEnableLoadMoreWhenContentNotFull = true;
    //</editor-fold>

    //<editor-fold desc="ScrollBoundaryDecider">
    @Override
    public boolean canRefresh(View content) {
        if (boundary != null) {
            Log.d("SmartRefreshLayout", "滚动 3 ");

            return boundary.canRefresh(content);
        }
        //mActionEvent == null 时 canRefresh 不会动态递归搜索
        boolean crboo = SmartUtil.canRefresh(content, mActionEvent);
        Log.d("SmartRefreshLayout", "滚动 4 ：" + crboo);
        return crboo;
    }

    @Override
    public boolean canLoadMore(View content) {
        if (boundary != null) {
            return boundary.canLoadMore(content);
        }
        //mActionEvent == null 时 canLoadMore 不会动态递归搜索
        return SmartUtil.canLoadMore(content, mActionEvent, mEnableLoadMoreWhenContentNotFull);
    }
    //</editor-fold>
}
