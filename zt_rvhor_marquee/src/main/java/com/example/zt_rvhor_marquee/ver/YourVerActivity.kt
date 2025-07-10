package com.example.zt_rvhor_marquee.ver

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.example.zt_rvhor_marquee.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class YourVerActivity : AppCompatActivity() {

    var mAutoTask: Disposable? = null
    lateinit var mSmoothScroll: LinearSmoothScroller
    var mRv: AutoScrollRecyclerView? = null
    var mRvTwo: AutoScrollRecyclerView? = null

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, YourVerActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var data = arrayListOf<String>()
        for (i in 1..15) {
            data.add("测试数据$i")
        }
        mRv = findViewById(R.id.mRv)
        mRvTwo = findViewById(R.id.mRvTwo)

        mRv?.layoutManager = LinearLayoutManager(this)
        mRv?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRv?.adapter = MainAdatper(data)

        //自定义LinearSmoothScroller，重写方法，滚动item至顶部，控制滚动速度
        mSmoothScroll = object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                // 移动一英寸需要花费3ms
                return 3f / (displayMetrics?.density ?: 1f)
            }

        }

        mRvTwo?.layoutManager = LinearLayoutManager(this)
        mRvTwo?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRvTwo?.adapter = MainAdatper(data)

    }

    override fun onStart() {
        super.onStart()
        startAuto()
        mRvTwo?.start()
    }

    override fun onStop() {
        super.onStop()
        stopAuto()
        mRvTwo?.stop()
    }

    fun startAuto() {
        if (mAutoTask != null && !(mAutoTask?.isDisposed ?: true))
            mAutoTask?.dispose()

        mAutoTask =
            Observable.interval(1, 2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //定位到指定项如果该项可以置顶就将其置顶显示
                    mSmoothScroll.targetPosition = it.toInt()
                    (mRv?.layoutManager as LinearLayoutManager).startSmoothScroll(mSmoothScroll)
                }
    }

    fun stopAuto() {
        if (mAutoTask != null && (mAutoTask?.isDisposed ?: true)) {
            mAutoTask?.dispose()
            mAutoTask = null
        }
    }
}
