package com.zzt;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.zt_rv_include_rv.BuildConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author: zeting
 * @date: 2025/4/28
 */
public class MoreRVUtil {
    private HashSet<RecyclerView> observerList = new HashSet<>();
    //    private List<RecyclerView> observerList = new ArrayList<>();
    private int firstPos = -1;
    private int firstOffset = -1;


    //多条recycleview联动
    public void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        //为每一个recycleview创建layoutManager
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //todo
        // 通过移动layoutManager来实现列表滑动  此行是让新加载的item条目保持跟已经滑动的recycleview位置保持一致
        // 也就是上拉加载更多的时候  保证新加载出来的item 跟已经滑动的item位置保持一致
        if (layoutManager != null && firstPos > 0 && firstOffset > 0) {
            Log.e("ZZT", "移动 ：firstPos：" + (firstPos + 1) + " offset:" + firstOffset + " size:" + observerList.size());

            layoutManager.scrollToPositionWithOffset(firstPos + 1, firstOffset);
        }
        // 添加所有的 recyclerView
        observerList.add(recyclerView);
        //当触摸条目的时候 停止滑动
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        for (RecyclerView rv : observerList) {
                            rv.stopScroll();
                        }
                }
                return false;
            }
        });
        //添加当前滑动recycleview的滑动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //获取显示第一个item的位置
                int firstPos1 = linearLayoutManager.findFirstVisibleItemPosition();
                View firstVisibleItem = linearLayoutManager.getChildAt(0);
                if (firstVisibleItem != null) {
                    //获取第一个item的偏移量
                    int firstRight = linearLayoutManager.getDecoratedRight(firstVisibleItem);

                    Log.w("ZZT", "移动 ：firstPos：" + firstPos1 + " offset:" + firstRight + " size:" + observerList.size());

                    //遍历其它的所有的recycleview条目
                    for (RecyclerView rv : observerList) {
                        if (recyclerView != rv) {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
                            if (layoutManager != null) {
                                firstPos = firstPos1;
                                firstOffset = firstRight;

                                Log.d("ZZT", "滑动：firstPos：" + firstPos + " offset:" + firstOffset);

                                //通过当前显示item的位置和偏移量的位置来置顶recycleview 也就是同步其它item的移动距离
                                layoutManager.scrollToPositionWithOffset(firstPos + 1, firstRight);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}
