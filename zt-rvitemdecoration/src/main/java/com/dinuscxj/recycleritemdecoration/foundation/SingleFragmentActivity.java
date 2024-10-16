package com.dinuscxj.recycleritemdecoration.foundation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.zzt.zt_rvitemdecoration.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected int getLayoutId() {
        return R.layout.activity_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        replaceFragment();
    }

    protected void replaceFragment() {
        Fragment fragment = createFragment();

        if (fragment == null) {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, createFragment())
                .commit();
    }

    protected abstract Fragment createFragment();
}