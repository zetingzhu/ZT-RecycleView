package com.example.zt_rvhor_marquee.blibli

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zt_rvhor_marquee.R
import com.example.zt_rvhor_marquee.danmurv.countdown2
import com.example.zt_rvhor_marquee.databinding.ActivityBliBliDanmuBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.controller.IDanmakuView.OnDanmakuClickListener
import master.flame.danmaku.danmaku.loader.IllegalDataException
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.util.IOUtils
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

class BliBliDanmuAct : AppCompatActivity(), View.OnClickListener {
    var binding: ActivityBliBliDanmuBinding? = null
    private var mDanmakuView: IDanmakuView? = null

    private var mMediaController: View? = null


    private var mBtnHideDanmaku: Button? = null

    private var mBtnShowDanmaku: Button? = null

    private var mBtnPauseDanmaku: Button? = null

    private var mBtnResumeDanmaku: Button? = null

    private var mBtnSendDanmaku: Button? = null

    private var mBtnSendDanmakuTextAndImage: Button? = null

    private var mBtnSendDanmakus: Button? = null
    private var mContext: DanmakuContext? = null
    var mJob: Job? = null

    private val mCacheStufferAdapter: BaseCacheStuffer.Proxy = object : BaseCacheStuffer.Proxy() {
        private var mDrawable: Drawable? = null

        override fun prepareDrawing(danmaku: BaseDanmaku, fromWorkerThread: Boolean) {
            if (danmaku.text is Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                object : Thread() {
                    override fun run() {
                        val url = "http://www.bilibili.com/favicon.ico"
                        var inputStream: InputStream? = null
                        var drawable = mDrawable
                        if (drawable == null) {
                            try {
                                val urlConnection = URL(url).openConnection()
                                inputStream = urlConnection.getInputStream()
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap")
                                mDrawable = drawable
                            } catch (e: MalformedURLException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } finally {
                                IOUtils.closeQuietly(inputStream)
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100)
                            val spannable = createSpannable(drawable)
                            danmaku.text = spannable
                            if (mDanmakuView != null) {
                                mDanmakuView!!.invalidateDanmaku(danmaku, false)
                            }
                            return
                        }
                    }
                }.start()
            }
        }

        override fun releaseResource(danmaku: BaseDanmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    }
    private var mParser: BaseDanmakuParser? = null

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, BliBliDanmuAct::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBliBliDanmuBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
    }

    private fun createParser(stream: InputStream?): BaseDanmakuParser {
        if (stream == null) {
            return object : BaseDanmakuParser() {
                override fun parse(): Danmakus {
                    return Danmakus()
                }
            }
        }

        val loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI)

        try {
            loader.load(stream)
        } catch (e: IllegalDataException) {
            e.printStackTrace()
        }
        val parser: BaseDanmakuParser = BiliDanmukuParser()
        val dataSource = loader.dataSource
        parser.load(dataSource)
        return parser
    }

    private fun findViews() {
        mMediaController = findViewById<View>(R.id.media_controller)
        mBtnHideDanmaku = findViewById<View>(R.id.btn_hide) as Button
        mBtnShowDanmaku = findViewById<View>(R.id.btn_show) as Button
        mBtnPauseDanmaku = findViewById<View>(R.id.btn_pause) as Button
        mBtnResumeDanmaku = findViewById<View>(R.id.btn_resume) as Button
        mBtnSendDanmaku = findViewById<View>(R.id.btn_send) as Button
        mBtnSendDanmakuTextAndImage = findViewById<View>(R.id.btn_send_image_text) as Button
        mBtnSendDanmakus = findViewById<View>(R.id.btn_send_danmakus) as Button
        mBtnHideDanmaku?.setOnClickListener(this)
        mMediaController?.setOnClickListener(this)
        mBtnShowDanmaku?.setOnClickListener(this)
        mBtnPauseDanmaku?.setOnClickListener(this)
        mBtnResumeDanmaku?.setOnClickListener(this)
        mBtnSendDanmaku?.setOnClickListener(this)
        mBtnSendDanmakuTextAndImage?.setOnClickListener(this)
        mBtnSendDanmakus?.setOnClickListener(this)

        // 设置最大显示行数
        val maxLinesPair = HashMap<Int, Int>()
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5 // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        val overlappingEnablePair = HashMap<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true

        mDanmakuView = findViewById(R.id.sv_danmaku)
        mContext = DanmakuContext.create()
        mContext?.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3f)
            ?.setDuplicateMergingEnabled(false)
            ?.setScrollSpeedFactor(1.2f)
            ?.setScaleTextSize(1.2f)
            ?.setCacheStuffer(
                SpannedCacheStuffer(),
                mCacheStufferAdapter
            ) // 图文混排使用SpannedCacheStuffer
            //        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
            ?.setMaximumLines(maxLinesPair)
            ?.preventOverlapping(overlappingEnablePair)
            ?.setDanmakuMargin(40)
        if (mDanmakuView != null) {
            mParser = createParser(null)
            mDanmakuView?.setCallback(object : DrawHandler.Callback {
                override fun updateTimer(timer: DanmakuTimer) {
                }

                override fun drawingFinished() {
                }

                override fun danmakuShown(danmaku: BaseDanmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
                }

                override fun prepared() {
                    mDanmakuView?.start()
                }
            })
            mDanmakuView?.setOnDanmakuClickListener(object : OnDanmakuClickListener {
                override fun onDanmakuClick(danmakus: IDanmakus): Boolean {
                    Log.d("DFM", "onDanmakuClick: danmakus size:" + danmakus.size())
                    val latest = danmakus.last()
                    if (null != latest) {
                        Log.d("DFM", "onDanmakuClick: text of latest danmaku:" + latest.text)
                        return true
                    }
                    return false
                }

                override fun onDanmakuLongClick(danmakus: IDanmakus): Boolean {
                    return false
                }

                override fun onViewClick(view: IDanmakuView): Boolean {
                    mMediaController?.setVisibility(View.VISIBLE)
                    return false
                }
            })
            mDanmakuView?.prepare(mParser, mContext)
            mDanmakuView?.showFPS(true)
            mDanmakuView?.enableDanmakuDrawingCache(true)
        }

    }

    override fun onPause() {
        super.onPause()
        if (mDanmakuView != null && mDanmakuView?.isPrepared() == true) {
            mDanmakuView?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mDanmakuView != null && mDanmakuView?.isPrepared() == true && mDanmakuView?.isPaused()!!) {
            mDanmakuView?.resume()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView?.release()
            mDanmakuView = null
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView?.release()
            mDanmakuView = null
        }
    }


    override fun onClick(v: View) {
        if (v === mMediaController) {
            mMediaController?.setVisibility(View.GONE)
        }
        if (mDanmakuView == null || !mDanmakuView!!.isPrepared()) return
        if (v === mBtnHideDanmaku) {
            mDanmakuView?.hide()
        } else if (v === mBtnShowDanmaku) {
            mDanmakuView?.show()
        } else if (v === mBtnPauseDanmaku) {
            mDanmakuView?.pause()
        } else if (v === mBtnResumeDanmaku) {
            mDanmakuView?.resume()
        } else if (v === mBtnSendDanmaku) {
            // 发一条纯文字弹幕
            addDanmaku(false)
        } else if (v === mBtnSendDanmakuTextAndImage) {
            addDanmaKuShowTextAndImage(false)
        } else if (v === mBtnSendDanmakus) {
            val b = mBtnSendDanmakus?.getTag()
            if (b == null || b == false) {
                mBtnSendDanmakus?.setText(R.string.cancel_sending_danmakus)
                mJob = countdown2(duration = Long.MAX_VALUE, interval = 1000) {
                    addDanmaku(true)
                }.launchIn(MainScope())

                mBtnSendDanmakus?.setTag(true)
            } else {
                mBtnSendDanmakus?.setText(R.string.send_danmakus)
                mBtnSendDanmakus?.setTag(false)

                mJob?.cancel()
            }
        }
    }

    fun addDanmaku(islive: Boolean) {
        val danmaku: BaseDanmaku? =
            mContext?.mDanmakuFactory?.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
        if (danmaku == null || mDanmakuView == null) {
            return
        }
        danmaku.text = "这是一条弹幕" + System.nanoTime()
        danmaku.padding = 5
        danmaku.priority = 0 // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive
        danmaku.time = mDanmakuView?.getCurrentTime() ?: 0 + 1200
        danmaku.textSize = 25f
        danmaku.textColor = Color.RED
        danmaku.textShadowColor = Color.WHITE
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = Color.GREEN
        mDanmakuView?.addDanmaku(danmaku)
    }

    private fun addDanmaKuShowTextAndImage(islive: Boolean) {
        val danmaku: BaseDanmaku? =
            mContext?.mDanmakuFactory?.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
        danmaku?.let { danmaku ->
            val drawable = resources.getDrawable(R.mipmap.ic_launcher)
            drawable.setBounds(0, 0, 100, 100)
            val spannable = createSpannable(drawable)
            danmaku.text = spannable
            danmaku.padding = 5
            danmaku.priority = 1 // 一定会显示, 一般用于本机发送的弹幕
            danmaku.isLive = islive
            danmaku.time = mDanmakuView?.getCurrentTime() ?: 0 + 1200
            danmaku.textSize = 25f
            danmaku.textColor = Color.RED
            danmaku.textShadowColor =
                0 // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
            danmaku.underlineColor = Color.GREEN
            mDanmakuView?.addDanmaku(danmaku)
        }
    }

    private fun createSpannable(drawable: Drawable): SpannableStringBuilder {
        val text = "bitmap"
        val spannableStringBuilder = SpannableStringBuilder(text)
        val span = ImageSpan(drawable) //ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.append("图文混排")
        spannableStringBuilder.setSpan(
            BackgroundColorSpan(Color.parseColor("#8A2233B1")),
            0,
            spannableStringBuilder.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        return spannableStringBuilder
    }
}