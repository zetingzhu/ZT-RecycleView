package com.zzt.downv3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzt.downtimer.R;
import com.zzt.entiy.ItemListData;

public class RvListActV3 extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, RvListActV3.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rv_list_act_v3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        RecyclerView rv_list = findViewById(R.id.rv_list);
        RvAdapterV3 adapterV3 = new RvAdapterV3(ItemListData.getInstance().dataList());
        rv_list.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rv_list.setAdapter(adapterV3);
    }
}