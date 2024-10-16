package com.dinuscxj.recyclDecoration;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;

/**
 * @author: zeting
 * @date: 2024/10/16
 */
public class RvGridItemDecoration extends Y_DividerItemDecoration {
    private static final String TAG = RvGridItemDecoration.class.getSimpleName();

    // 分割线颜色
    private @ColorInt int color = 0x00000000;
    // 分割线高度
    private float height;

    public RvGridItemDecoration(Context context) {
        super(context);
    }

    public RvGridItemDecoration(Context context, int color, float height) {
        super(context);
        this.color = color;
        this.height = height;
    }

    /**
     * 设置颜色和高度
     *
     * @param color
     * @param height
     */
    public void setDividerColorAndHeight(@ColorInt int color, float height) {
        this.color = color;
        this.height = height;
    }

    @Nullable
    @Override
    public Y_Divider getDivider(int itemPosition, int childCount, int mSpanCount) {
        Log.d(TAG, "分割线 pos:" + itemPosition + " count:" + childCount + " spanCount:" + mSpanCount);
        Y_Divider divider = null;
        if (mSpanCount > 1) {
            // 取模
            int mod = itemPosition % mSpanCount;
            if (mod == 0) {
                // 取模第一个
                divider = new Y_DividerBuilder()
                        .setBottomSideLine(true, color, height, 0, 0)
                        .setRightSideLine(true, color, height / 2, 0, 0)
                        .create();
            } else if (mod == mSpanCount - 1) {
                // 取模最后一个
                divider = new Y_DividerBuilder()
                        .setBottomSideLine(true, color, height, 0, 0)
                        .setLeftSideLine(true, color, height / 2, 0, 0)
                        .create();
            } else {
                // 其他的
                divider = new Y_DividerBuilder()
                        .setBottomSideLine(true, color, height, 0, 0)
                        .setLeftSideLine(true, color, height / 2, 0, 0)
                        .setRightSideLine(true, color, height / 2, 0, 0)
                        .create();
            }
        } else {
            divider = new Y_DividerBuilder()
                    .setBottomSideLine(true, color, height, 0, 0)
                    .create();

        }
        return divider;
    }

}
