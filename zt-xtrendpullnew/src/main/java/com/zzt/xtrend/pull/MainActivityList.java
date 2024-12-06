package com.zzt.xtrend.pull;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzt.adapter.StartActivityRecyclerAdapter;
import com.zzt.entity.StartActivityDao;
import com.zzt.views.ZtVerticalRecycleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivityList extends AppCompatActivity {
    ZtVerticalRecycleView zvrv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

    }

    private void initView() {
        zvrv_list = findViewById(R.id.zvrv_list);

        List<StartActivityDao> itemList = new ArrayList<>();
        itemList.add(new StartActivityDao("Scroll pull", "ScrollView 下拉，上拉刷新", ScrollViewPullAct.class));
        itemList.add(new StartActivityDao("RecycleView pull", "RecycleView 下拉，上拉刷新", RecycleviewPullAct.class));
        itemList.add(new StartActivityDao("LinearLayout pull", "LinearLayout 嵌套RecycleViewe 下拉，上拉刷新", LinearLayoutPullActV2.class));
        itemList.add(new StartActivityDao("LinearLayout pull", "LinearLayout 没有嵌套，普通布局 下拉，上拉刷新", LinearLayoutPullActV3.class));

        zvrv_list.addListNotifyAdapter(itemList, new StartActivityRecyclerAdapter.OnItemClickListener<StartActivityDao>() {
            @Override
            public void onItemClick(View itemView, int position, StartActivityDao data) {

            }
        });
    }
}