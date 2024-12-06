package com.scwang.smart.refresh.layout.listener;

import android.view.View;

import com.scwang.smart.refresh.layout.api.RefreshKernel;

/**
 * @author: zeting
 * @date: 2024/12/6
 * 添加自定义 Behavior 处理
 */
public interface MyBehaviorListener {

    /**
     * 自定义的 Behavior 返回数据自己处理
     *
     * @param content
     * @param kernel
     * @param listener
     */
    void checkCoordinatorLayout(View content, RefreshKernel kernel, CoordinatorLayoutListener listener);
}
