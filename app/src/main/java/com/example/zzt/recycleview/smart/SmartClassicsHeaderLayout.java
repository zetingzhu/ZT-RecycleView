package com.example.zzt.recycleview.smart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.zzt.recycleview.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author: zeting
 * @date: 2024/12/6
 * 普通的下拉头
 */
public class SmartClassicsHeaderLayout extends SimpleComponent implements RefreshHeader {
    protected TextView mLastUpdateText; // 更新时间
    protected TextView mHeaderTimeViewTitle;//最后更新时间的标题
    protected DateFormat mLastUpdateFormat; // 时间格式


    //<editor-fold desc="RelativeLayout">
    public SmartClassicsHeaderLayout(Context context) {
        this(context, null);
    }

    public SmartClassicsHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.pull_to_refresh_header, this);

        mLastUpdateText = findViewById(R.id.pull_to_refresh_header_time);
        mHeaderTimeViewTitle = findViewById(R.id.pull_to_refresh_last_update_time_text);

        mLastUpdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

        try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                @SuppressLint("RestrictedApi")
                List<Fragment> fragments = manager.getFragments();
                if (fragments.size() > 0) {
                    setLastUpdateTime(new Date());
                    return;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        setLastUpdateTime(new Date());

    }
    //</editor-fold>

    /**
     * 设置最后更新的时间文本
     *
     * @param label 文本
     */
    public void setLastUpdatedLabel(CharSequence label) {
        // 如果最后更新的时间的文本是空的话，隐藏前面的标题
        if (mHeaderTimeViewTitle != null) {
            mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);
        }
        if (mLastUpdateText != null) {
            mLastUpdateText.setText(label);
        }
    }


    public SmartClassicsHeaderLayout setLastUpdateTime(Date time) {
        String formatTime = mLastUpdateFormat.format(time);
        mLastUpdateText.setText(formatTime);
        return this;
    }
}
