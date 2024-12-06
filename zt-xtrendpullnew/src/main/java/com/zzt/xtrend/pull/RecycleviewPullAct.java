package com.zzt.xtrend.pull;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.adapter.StartActivityRecyclerAdapter;
import com.zzt.entity.StartActivityDao;
import com.zzt.xtrend.pulltorefresh.ILoadingLayout;
import com.zzt.xtrend.pulltorefresh.PullToRefreshBase;
import com.zzt.xtrend.pulltorefresh.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecycleviewPullAct extends AppCompatActivity {

    PullToRefreshRecyclerView pullrv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recycleview_pull);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        pullrv_list = findViewById(R.id.pullrv_list);
        pullrv_list.setPullLoadEnabled(true);
        pullrv_list.setPullRefreshEnabled(true);
        pullrv_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullrv_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullrv_list.onPullUpRefreshComplete();
                        pullrv_list.onPullDownRefreshComplete();
                    }
                }, 2000L);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                pullrv_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullrv_list.onPullUpRefreshComplete();
                        pullrv_list.onPullDownRefreshComplete();
                    }
                }, 2000L);
            }
        });

        RecyclerView refreshableView = pullrv_list.getRefreshableView();

        List<StartActivityDao> itemList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            itemList.add(new StartActivityDao("标题 > " + i, "内容 >> " + i));
        }
        StartActivityRecyclerAdapter.setAdapterData(refreshableView, RecyclerView.VERTICAL, itemList, null);

    }
}