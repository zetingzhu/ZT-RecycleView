package com.example.zzt.recycleview.refresh.v1;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.zzt.recycleview.R;


/**
 * 这个类封装了下拉刷新的布局
 * 
 * @author Li Hong
 * @since 2013-7-30
 */
public class FooterLoadingLayout extends LoadingLayout {
    /**进度条*/
//    private ProgressBar mProgressBar;
    View loadingView;
    /** 显示的文本 */
    private TextView mHintView;
    
    /**
     * 构造方法
     * 
     * @param context context
     */
    public FooterLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public FooterLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     * 
     * @param context context
     */
    private void init(Context context) {
//        mProgressBar = (ProgressBar) findViewById(R.id.pull_to_load_footer_progressbar);
        loadingView =  findViewById(R.id.loadingView);
        loadingView.setVisibility(View.GONE);
        mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);
        
        setState(State.RESET);
    }
    
    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_load_footer, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    }

    @Override
    public int getContentSize() {
        View view = findViewById(R.id.pull_to_load_footer_content);
        if (null != view) {
            return view.getHeight();
        }
        
        return (int) (getResources().getDisplayMetrics().density * 40);
    }
    
    @Override
    protected void onStateChanged(State curState, State oldState) {
//        mProgressBar.setVisibility(View.GONE);
//        loadingView.setVisibility(View.GONE);
        mHintView.setVisibility(View.INVISIBLE);
        
        super.onStateChanged(curState, oldState);
    }
    
    @Override
    protected void onReset() {
//        mHintView.setText(R.string.pull_to_refresh_header_hint_loading);
//        mHintView.setText(R.string.s19_3);
    }

    @Override
    protected void onPullToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
//        mHintView.setText(R.string.pull_to_refresh_header_hint_normal2);
//        mHintView.setText(R.string.s19_2);
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
//        mHintView.setText(R.string.pull_to_refresh_header_hint_ready);
//        mHintView.setText(R.string.s19_2);
    }

    @Override
    protected void onRefreshing() {
//        mProgressBar.setVisibility(View.VISIBLE);
//        loadingView.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
//        mHintView.setText(R.string.pull_to_refresh_header_hint_loading);
//        mHintView.setText(R.string.s19_3);
    }
    
    @Override
    protected void onNoMoreData() {
        mHintView.setVisibility(View.VISIBLE);
//        mHintView.setText(R.string.pushmsg_center_no_more_msg);
//        mHintView.setText(R.string.s19_2);
    }
}
