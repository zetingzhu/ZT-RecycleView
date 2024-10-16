package com.zzt.act;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.zzt.zt_rvitemdecoration.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnShader;
    private Button mBtnGridDivider;
    private Button mBtnPinnedHeader;
    private Button mBtnLinearDivider, btn_normal_decor, grid_dram_offsets, grid_v3,
            btn_ver_v3, grid_v4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnShader = (Button) findViewById(R.id.shader);
        mBtnGridDivider = (Button) findViewById(R.id.grid_offsets);
        mBtnPinnedHeader = (Button) findViewById(R.id.pinned_header);
        mBtnLinearDivider = (Button) findViewById(R.id.linear_divider);
        btn_normal_decor = findViewById(R.id.btn_normal_decor);
        grid_dram_offsets = findViewById(R.id.grid_dram_offsets);
        grid_v3 = findViewById(R.id.grid_v3);
        btn_ver_v3 = findViewById(R.id.btn_ver_v3);
        grid_v4 = findViewById(R.id.grid_v4);

        mBtnShader.setOnClickListener(this);
        mBtnGridDivider.setOnClickListener(this);
        mBtnPinnedHeader.setOnClickListener(this);
        mBtnLinearDivider.setOnClickListener(this);
        btn_normal_decor.setOnClickListener(this);
        grid_dram_offsets.setOnClickListener(this);
        grid_v3.setOnClickListener(this);
        btn_ver_v3.setOnClickListener(this);
        grid_v4.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shader) {
            ShaderActivity.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.pinned_header) {
            PinnedHeaderActivity.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.linear_divider) {
            ActRvVerV1.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.btn_normal_decor) {
            ActRvVerV2.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.btn_ver_v3) {
            ActRvVerV3.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.grid_offsets) {
            ActRvGridV1.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.grid_dram_offsets) {
            ActRvGridV2.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.grid_v3) {
            ActRvGridV3.startActivity(MainActivity.this);
        } else if (v.getId() == R.id.grid_v4) {
            ActRvGridV4.startActivity(MainActivity.this);
        }
    }
}
