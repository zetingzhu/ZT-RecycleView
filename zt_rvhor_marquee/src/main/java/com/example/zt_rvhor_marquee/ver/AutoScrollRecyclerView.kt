package com.example.zt_rvhor_marquee.ver

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created by kermitye on 2018/10/11 16:40
 */
class AutoScrollRecyclerView(mContext: Context, attrs: AttributeSet?) :
    RecyclerView(mContext, attrs) {

    private var mAutoTask: Disposable? = null

    //禁止手动滑动
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    fun start() {
        if (mAutoTask != null && !mAutoTask!!.isDisposed) {
            mAutoTask!!.dispose()
        }
        mAutoTask = Observable.interval(1000, 100, TimeUnit.MILLISECONDS).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribe { smoothScrollBy(0, 20) }
    }

    fun stop() {
        if (mAutoTask != null && !mAutoTask!!.isDisposed) {
            mAutoTask!!.dispose()
            mAutoTask = null
        }
    }

}