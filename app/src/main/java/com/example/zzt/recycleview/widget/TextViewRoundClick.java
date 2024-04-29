package com.example.zzt.recycleview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author: zeting
 * @date: 2024/4/2
 * 移除点击事件滑动监听.只处理固定圆点内点击
 */
public class TextViewRoundClick extends AppCompatTextView {
    private static final String TAG = TextViewRoundClick.class.getSimpleName();

    float downX = 0;
    float downY = 0;
    float upX = 0;
    float upY = 0;
    // 最小距离
    double minDistance = 20D;

    public TextViewRoundClick(@NonNull Context context) {
        super(context);
    }

    public TextViewRoundClick(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewRoundClick(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                upX = event.getX();
                upY = event.getY();
//                Log.d(TAG, "触发点击 UP downX：" + downX + " downY:" + downY + " upX:" + upX + " upY:" + upY);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 求出两点距离
     */
    public double calculateDistance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    @Override
    public boolean performClick() {
        double distance = calculateDistance(downX, downY, upX, upY);
//        Log.d(TAG, "触发点击事件 performClick  downX：" + downX + " downY:" + downY + " upX:" + upX + " upY:" + upY + " distance>" + distance);
        if (distance < minDistance) {
            return super.performClick();
        }
        return true;
    }
}
