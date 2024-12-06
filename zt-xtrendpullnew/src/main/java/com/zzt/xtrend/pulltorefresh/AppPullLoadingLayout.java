package com.zzt.xtrend.pulltorefresh;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.zzt.xtrend.pull.R;


/**
 * 这个类封装了loading动画
 */
public class AppPullLoadingLayout extends RelativeLayout {

    /**
     * 旋转图片
     */
    private ImageView imgView;
    /**
     * 提示TextView
     */
    private TextView apptextview;
    /**
     * 旋转的动画
     */
    private Animation mRotateAnimation;
    private Matrix rotationMatrix;

    /**
     * 构造方法
     *
     * @param context context
     */
    public AppPullLoadingLayout(Context context) {
        super(context);
        init(context);
    }


    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public AppPullLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected View createLoadingView(Context context) {
        View container = LayoutInflater.from(context).inflate(
                R.layout.layout_pull_progressbar, null);
        return container;
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        addView(createLoadingView(context));
        imgView = (ImageView) findViewById(R.id.imgView);
        apptextview = (TextView) findViewById(R.id.apptextview);

        rotationMatrix = new Matrix();
        mRotateAnimation = AnimationUtils.loadAnimation(context,
                R.anim.rotate_loading);
        resetRotation();
        imgView.startAnimation(mRotateAnimation);
        if (context instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) context;
            owner.getLifecycle().addObserver(observer);
        }
    }

    public void onPull(float scale) {
        float angle = scale * 180f; // SUPPRESS CHECKSTYLE
        rotationMatrix.setRotate(angle);
        imgView.setImageMatrix(rotationMatrix);
    }

    /**
     * 重置动画
     */
    private void resetRotation() {
        imgView.clearAnimation();
        rotationMatrix.setRotate(0);
        imgView.setImageMatrix(rotationMatrix);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetRotation();
        imgView.startAnimation(mRotateAnimation);
    }

    private LifecycleObserver observer = new DefaultLifecycleObserver() {
        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onResume(owner);
            imgView.startAnimation(mRotateAnimation);
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onPause(owner);
            imgView.clearAnimation();
        }
    };
}
