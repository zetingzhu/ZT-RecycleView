package com.example.zzt.recycleview.rvitemdecor;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zzt.recycleview.R;
import com.zzt.utilcode.util.ScreenUtils;


/**
 * Author: ly
 * Date: 2021/3/24 16:07
 * Description: 财经日历时间标签间隔线
 */
public class EconomicsTimeItemDecoration extends RecyclerView.ItemDecoration {

    // 有图标
    private int topSelectPosition = -1;
    private String showTime = "";


    /**
     * 设置时间图标
     *
     * @param topSelectPosition
     */
    public void setTopPosition(int topSelectPosition, String showTime) {
        this.showTime = showTime;
        this.topSelectPosition = topSelectPosition;
    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int pos = parent.getChildAdapterPosition(child);
            if (topSelectPosition == pos && !showTime.isEmpty()) {
                int windowWidth = ScreenUtils.getAppScreenWidth();
                // 添加一个布局
                int saveCount = c.save();
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_bottom, null, true);
                TextView tv_calendar_time = view.findViewById(R.id.tv_calendar_time);
                tv_calendar_time.setText(showTime);
                int width = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.AT_MOST);
                int height = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.AT_MOST);
                view.measure(width, height);
                view.layout(0, 0, windowWidth, view.getMeasuredHeight());
                c.translate(0, child.getTop() - 10);
                view.draw(c);
                c.restoreToCount(saveCount);
            }
        }
    }
}
