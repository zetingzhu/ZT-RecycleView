package com.scwang.refresh.layout.activity.example;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.util.DynamicTimeFormat;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener;

/**
 * 全局指定默认的Header和Footer
 */
public class AssignDefaultExampleActivity extends AppCompatActivity {

   private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        install();

        setContentView(R.layout.activity_example_assign_default);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        /*
         * 以下代码仅仅为了演示效果而已，不是必须的
         * 关键代码在构造函数中
         */
        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        if (isFirstEnter) {
            isFirstEnter = false;
            //通过多功能监听接口实现 在第一次加载完成之后 自动刷新
            refreshLayout.setOnMultiListener(new SimpleMultiListener(){
                @Override
                public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
                    if (oldState == RefreshState.LoadFinish
                            && newState == RefreshState.None) {
                        refreshLayout.autoRefresh();
                        refreshLayout.setOnMultiListener(null);
                    }
                }
                @Override
                public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                    refreshLayout.finishLoadMore(2000);
                }
                @Override
                public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    refreshLayout.finishRefresh(3000);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restore();
    }

    /*
     * 关键代码，需要在布局生成之前设置，建议代码放在 Application 中
     */
    private static void install() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                ClassicsHeader header = new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.FixedBehind);
                header.setPrimaryColorId(R.color.colorPrimary);
                header.setAccentColorId(android.R.color.white);
                return header;//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setEnableLoadMoreWhenContentNotFull(true);//内容不满一页时候启用加载更多
                ClassicsFooter footer = new ClassicsFooter(context);
                footer.setBackgroundResource(android.R.color.white);
                footer.setSpinnerStyle(SpinnerStyle.FixedBehind);//设置为拉伸模式
                return footer;//指定为经典Footer，默认是 BallPulseFooter
            }
        });
    }

    //还原默认 Header
    private static void restore() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context).setTimeFormat(new DynamicTimeFormat("更新于 %s"));
            }
        });
    }
}
