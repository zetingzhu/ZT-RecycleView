package com.scwang.refresh.layout.activity.practice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.zzt.zt_smartrefreshlayout_sample.R;
import com.scwang.refresh.layout.util.StatusBarUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import java.util.Locale;

/**
 * 网页-Github
 */
public class WebViewPracticeActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_webview);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        final WebView webView = findViewById(R.id.webView);
        final RefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener((OnRefreshListener) refreshLayout1 -> webView.loadUrl("https://github.com/scwang90/SmartRefreshLayout"));
        refreshLayout.autoRefresh();


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
                refreshLayout.finishRefresh();
                view.loadUrl(String.format(Locale.CHINA, "javascript:document.body.style.paddingTop='%fpx'; void 0", SmartUtil.px2dp(webView.getPaddingTop())));
            }
        });

        //状态栏透明和间距处理
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, webView);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setMargin(this, findViewById(R.id.header));
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurView));
    }

}
