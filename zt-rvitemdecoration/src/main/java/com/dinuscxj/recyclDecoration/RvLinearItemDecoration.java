package com.dinuscxj.recyclDecoration;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.yanyusong.y_divideritemdecoration.Y_Divider;
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder;
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration;
import com.zzt.zt_rvitemdecoration.R;

/**
 * @author: zeting
 * @date: 2024/10/16
 */
public class RvLinearItemDecoration extends Y_DividerItemDecoration {
    private static final String TAG = RvLinearItemDecoration.class.getSimpleName();
    // 垂直上下都有线
    public static final int VERTICAL_TOP_BOTTOM = 2;
    // 添加一个，没有最后一条线
    public static final int VERTICAL_NOT_BOTTOM = 3;
    // 垂直分割线
    public static final int VERTICAL = LinearLayout.VERTICAL;
    // 分割线类型
    private int mOrientation;

    // 分割线颜色
    private @ColorInt int color = 0x00000000;
    // 分割线高度
    private float height;

    public RvLinearItemDecoration(Context context, int mOrientation) {
        super(context);
        this.mOrientation = mOrientation;
    }

    public RvLinearItemDecoration(Context context, int mOrientation, int color, float height) {
        super(context);
        this.mOrientation = mOrientation;
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

        Log.d(TAG, "分割线 pos:" + itemPosition + " count:" + childCount);
        Y_Divider divider = null;
        if (VERTICAL_TOP_BOTTOM == mOrientation && itemPosition == 0) {
            divider = new Y_DividerBuilder()
                    .setTopSideLine(true, color, height, 0, 0)
                    .setBottomSideLine(true, color, height, 0, 0)
                    .create();
        } else if (VERTICAL_NOT_BOTTOM == mOrientation && itemPosition == childCount - 1) {
            divider = new Y_DividerBuilder().create();
        } else {
            divider = new Y_DividerBuilder()
                    .setBottomSideLine(true, color, height, 0, 0)
                    .create();

        }
        return divider;
    }

}
