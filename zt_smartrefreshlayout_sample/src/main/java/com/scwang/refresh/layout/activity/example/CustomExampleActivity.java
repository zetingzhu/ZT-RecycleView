package com.scwang.refresh.layout.activity.example;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.adapter.BaseRecyclerAdapter;
import com.scwang.refresh.layout.adapter.SmartViewHolder;
import com.scwang.smart.drawable.ProgressDrawable;
import com.scwang.smart.refresh.classics.ArrowDrawable;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import java.util.Arrays;
import java.util.Collection;

import static android.R.layout.simple_list_item_2;

/**
 * 自定义Header功能使用
 */
public class CustomExampleActivity extends AppCompatActivity {

    private BaseRecyclerAdapter<Void> mAdapter;

    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_custom);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(mAdapter = new BaseRecyclerAdapter<Void>(simple_list_item_2) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Void model, int position) {
                holder.text(android.R.id.text1, getString(R.string.item_example_number_title, position));
                holder.text(android.R.id.text2, getString(R.string.item_example_number_abstract, position));
                holder.textColorId(android.R.id.text2, R.color.colorTextAssistant);
            }
        });

        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshLayout1 -> refreshLayout1.getLayout().postDelayed(() -> {
            mAdapter.refresh(initData());
            refreshLayout1.finishRefresh();
        }, 2000));

        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setHeaderHeight(60);

        //触发自动刷新
        if (isFirstEnter) {
            isFirstEnter = false;
            refreshLayout.autoRefresh();
        } else {
            mAdapter.refresh(initData());
        }

    }

    private Collection<Void> initData() {
        return Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

    public static class ClassicsHeader extends LinearLayout implements RefreshHeader {

        private final TextView mHeaderText;//标题文本
        private final ImageView mArrowView;//下拉箭头
        private final ImageView mProgressView;//刷新动画视图
        private final ProgressDrawable mProgressDrawable;//刷新动画

        public ClassicsHeader(Context context) {
            this(context, null);
        }
        public ClassicsHeader(Context context, AttributeSet attrs) {
            super(context, attrs, 0);
            setGravity(Gravity.CENTER);
            mHeaderText = new TextView(context);
            mProgressDrawable = new ProgressDrawable();
            mArrowView = new ImageView(context);
            mProgressView = new ImageView(context);
            mProgressView.setImageDrawable(mProgressDrawable);
            mArrowView.setImageDrawable(new ArrowDrawable());
            addView(mProgressView, SmartUtil.dp2px(20), SmartUtil.dp2px(20));
            addView(mArrowView, SmartUtil.dp2px(20), SmartUtil.dp2px(20));
            addView(new Space(context), SmartUtil.dp2px(20), SmartUtil.dp2px(20));
            addView(mHeaderText, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            setMinimumHeight(SmartUtil.dp2px(60));
        }
        @NonNull
        public View getView() {
            return this;//真实的视图就是自己，不能返回null
        }
        @NonNull
        @Override
        public SpinnerStyle getSpinnerStyle() {
            return SpinnerStyle.Translate;//指定为平移，不能null
        }

        @Override
        public void onStartAnimator(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
            mProgressDrawable.start();//开始动画
        }

        @Override
        public int onFinish(@NonNull RefreshLayout layout, boolean success) {
            mProgressDrawable.stop();//停止动画
            mProgressView.setVisibility(GONE);//隐藏动画
            if (success){
                mHeaderText.setText("刷新完成");
            } else {
                mHeaderText.setText("刷新失败");
            }
            return 500;//延迟500毫秒之后再弹回
        }

        @Override
        public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
            switch (newState) {
                case None:
                case PullDownToRefresh:
                    mHeaderText.setText("下拉开始刷新");
                    mArrowView.setVisibility(VISIBLE);//显示下拉箭头
                    mProgressView.setVisibility(GONE);//隐藏动画
                    mArrowView.animate().rotation(0);//还原箭头方向
                    break;
                case Refreshing:
                    mHeaderText.setText("正在刷新");
                    mProgressView.setVisibility(VISIBLE);//显示加载动画
                    mArrowView.setVisibility(GONE);//隐藏箭头
                    break;
                case ReleaseToRefresh:
                    mHeaderText.setText("释放立即刷新");
                    mArrowView.animate().rotation(180);//显示箭头改为朝上
                    break;
            }
        }

        @Override
        public void setPrimaryColors(int... colors) {

        }

        @Override
        public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

        }

        @Override
        public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

        }

        @Override
        public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

        }

        @Override
        public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

        }

        @Override
        public boolean isSupportHorizontalDrag() {
            return false;
        }

        @Override
        public boolean autoOpen(int duration, float dragRate, boolean animationOnly) {
            return false;
        }
    }
}
