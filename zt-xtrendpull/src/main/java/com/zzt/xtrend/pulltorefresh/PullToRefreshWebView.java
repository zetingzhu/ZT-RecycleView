package com.zzt.xtrend.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 封装了WebView的下拉刷新
 * 
 * @author Li Hong
 * @since 2013-8-22
 */
public class PullToRefreshWebView extends PullToRefreshBase<WebView> {
    /**
     * 构造方法
     * 
     * @param context context
     */
    public PullToRefreshWebView(Context context) {
    	super(context);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }
    
    /**
     * 构造方法
     * 
     * @param context context
     * @param attrs attrs
     * @param defStyle defStyle
     */
    public PullToRefreshWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     */
    @Override
    protected WebView createRefreshableView(Context context, AttributeSet attrs) {
        WebView webView = new WebView(context,attrs);
        return webView;
    }

    /**
     */
    @Override
    protected boolean isReadyForPullDown() {
        return mRefreshableView.getScrollY() == 0;
    }

    /**
     */
    @Override
    protected boolean isReadyForPullUp() {
        float exactContentHeight = (float)Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());
        return mRefreshableView.getScrollY() >= (exactContentHeight - mRefreshableView.getHeight());
    }

}
