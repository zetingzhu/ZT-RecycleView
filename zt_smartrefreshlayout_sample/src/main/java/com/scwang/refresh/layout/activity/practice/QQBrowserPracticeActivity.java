package com.scwang.refresh.layout.activity.practice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.util.StatusBarUtil;

import ezy.ui.layout.LoadingLayout;

/**
 * QQ浏览器-Github
 */
public class QQBrowserPracticeActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_qqbrowser);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        final LoadingLayout loading = findViewById(R.id.loading);

        final WebView webView = findViewById(R.id.webView);
        webView.loadUrl("https://github.com/scwang90/SmartRefreshLayout");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loading.showContent();
            }
        });

        //状态栏透明和间距处理
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
    }

}
