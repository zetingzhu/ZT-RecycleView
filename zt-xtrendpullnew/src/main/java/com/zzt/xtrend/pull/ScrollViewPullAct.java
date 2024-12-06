package com.zzt.xtrend.pull;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.zzt.xtrend.pulltorefresh.PullToRefreshBase;
import com.zzt.xtrend.pulltorefresh.PullToRefreshNestedScrollViewV2;

public class ScrollViewPullAct extends AppCompatActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, ScrollViewPullAct.class);
        context.startActivity(starter);
    }

    PullToRefreshNestedScrollViewV2 pull_scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scroll_pull);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        pull_scroll = findViewById(R.id.pull_scroll);
        pull_scroll.setPullLoadEnabled(true);
        pull_scroll.setPullRefreshEnabled(true);
        pull_scroll.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<NestedScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {
                pull_scroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pull_scroll.onPullUpRefreshComplete();
                        pull_scroll.onPullDownRefreshComplete();
                    }
                }, 2000L);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {
                pull_scroll.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pull_scroll.onPullUpRefreshComplete();
                        pull_scroll.onPullDownRefreshComplete();
                    }
                }, 2000L);
            }
        });
    }
}