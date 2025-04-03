package com.zzt.act;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.TokenWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.header.TwoLevelHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.zzt.decoration.DividerDrawable;
import com.zzt.decoration.RecycleViewDecorationRemovePos;
import com.zzt.util.AdapterH;
import com.zzt.util.DataListUtil;
import com.zzt.zt_github_rv_sample.R;

public class ActRecycleViewV23 extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ActRecycleViewV23.class);
        context.startActivity(starter);
    }

    RecyclerView rv_list;
    AdapterH mAdapterV1;
    SmartRefreshLayout refL_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_act_recycle_view_v23);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        rv_list = findViewById(R.id.rv_list);

        refL_view = findViewById(R.id.refL_view);
//        refL_view.setRefreshHeader(new ClassicsHeader(this));
        refL_view.setRefreshHeader(new TwoLevelHeader(this));
        refL_view.setRefreshFooter(new ClassicsFooter(this));
        refL_view.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refL_view.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });

        mAdapterV1 = new AdapterH();
        rv_list.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        //添加自定义分割线
        RecycleViewDecorationRemovePos decoration = new RecycleViewDecorationRemovePos(this.getBaseContext(), RecycleViewDecorationRemovePos.VERTICAL_TOB_BOTTOM);
        decoration.setDrawable(new DividerDrawable(ContextCompat.getColor(this.getBaseContext(), R.color.white), 20));
        rv_list.addItemDecoration(decoration);
        rv_list.setAdapter(mAdapterV1);

        mAdapterV1.setListData(DataListUtil.INSTANCE.getList100(), rv_list);
    }
}