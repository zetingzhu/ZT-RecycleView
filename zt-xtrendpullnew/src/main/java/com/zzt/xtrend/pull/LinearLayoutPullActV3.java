package com.zzt.xtrend.pull;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzt.xtrend.pulltorefresh.PullToRefreshBase;
import com.zzt.xtrend.pulltorefresh.PullToRefreshLinearLayoutV2;
import com.zzt.xtrend.pulltorefresh.PullToRefreshLinearLayoutV3;

public class LinearLayoutPullActV3 extends AppCompatActivity {

    PullToRefreshLinearLayoutV3 rullll_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_linear_layout_pull_v3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        rullll_layout = findViewById(R.id.rullll_layout);
        rullll_layout.setPullLoadEnabled(true);
        rullll_layout.setPullRefreshEnabled(true);
        rullll_layout.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<LinearLayout>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<LinearLayout> refreshView) {
                rullll_layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rullll_layout.onPullUpRefreshComplete();
                        rullll_layout.onPullDownRefreshComplete();
                    }
                }, 2000L);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<LinearLayout> refreshView) {
                rullll_layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rullll_layout.onPullUpRefreshComplete();
                        rullll_layout.onPullDownRefreshComplete();
                    }
                }, 2000L);
            }
        });
    }
}