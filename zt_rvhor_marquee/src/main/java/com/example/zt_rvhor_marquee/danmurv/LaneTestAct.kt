package com.example.zt_rvhor_marquee.danmurv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.zt_rvhor_marquee.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn

class LaneTestAct : AppCompatActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, LaneTestAct::class.java)
            context.startActivity(starter)
        }
    }

    var rv_lane: RecyclerView? = null
    var addCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lane_test)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var laneList: List<LaneBean> = listOf(
            LaneBean("aaa0"),
            LaneBean("bbb1"),
            LaneBean("ccc2"),
            LaneBean("ddd3"),
            LaneBean("111111111cccdkfjsdlfjdslkfjlsdkfjlkdsfjksdl"),
            LaneBean("dddidddddddddddddddddddd"),
            LaneBean("eee4"),
            LaneBean("fff5"),
            LaneBean("ggg6"),
            LaneBean("hhh7"),
            LaneBean("iii8"),
            LaneBean("22222222222iiiklsdjflksdjflkjsdlkfjlskdfjlksdjflksdfs"),
            LaneBean("jjj9"),
            LaneBean("kkk10"),
            LaneBean("lll11"),
            LaneBean("mmm12"),
        )

        rv_lane = findViewById(R.id.rv_lane)
        val laneAdapter = LaneTestAdapter()
        rv_lane?.apply {
            layoutManager = LaneLayoutManager().apply {
                horizontalGap = 10
                verticalGap = 10
            }
            adapter = laneAdapter
            laneAdapter.submitList(laneList)
        }

        rv_lane?.let { rv ->
            countdown2(Long.MAX_VALUE, 50) {
                rv.smoothScrollBy(10, 0)
            }.launchIn(MainScope())
        }

        var button: Button = findViewById(R.id.button)
        button.setOnClickListener {
            addCount++
            var addList: MutableList<LaneBean> = mutableListOf()
            for (i in 1..5) {
                addList.add(LaneBean("${addCount}次，插入数据：${i}"))
            }
            laneAdapter.addData(addList)
        }

    }
}

data class LaneBean(var text: String)
