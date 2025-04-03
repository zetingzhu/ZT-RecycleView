package com.example.zzt.recycleview.act;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zzt.recycleview.R;
import com.example.zzt.recycleview.adapter.AdapterH;
import com.example.zzt.recycleview.rvitemdecor.RecycleViewDecorationRemovePos;
import com.example.zzt.recycleview.util.DataListUtil;
import com.zzt.decoration.DividerDrawable;

public class ActRecycleViewV23 extends AppCompatActivity {

    RecyclerView rv_list;
    AdapterH mAdapterV1;

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
        mAdapterV1 = new AdapterH();
        rv_list.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        //添加自定义分割线
        RecycleViewDecorationRemovePos decoration = new RecycleViewDecorationRemovePos(this.getBaseContext(), RecycleViewDecorationRemovePos.VERTICAL_TOP_BOTTOM);
        decoration.setDrawable(new DividerDrawable(ContextCompat.getColor(this.getBaseContext(), R.color.white_60), 20));
        rv_list.addItemDecoration(decoration);
        rv_list.setAdapter(mAdapterV1);

        mAdapterV1.setListData(DataListUtil.INSTANCE.getList100(), rv_list);
    }
}