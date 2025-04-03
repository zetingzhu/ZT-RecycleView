package com.zzt.downv2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzt.downtimer.R
import com.zzt.entiy.ItemTitle
import java.util.Random

class RvListActV2 : AppCompatActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, RvListActV2::class.java)
            context.startActivity(starter)
        }
    }

    var rv_list: RecyclerView? = null

    var mAdapter: RvAdapterV2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rv_list_act_v2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }


    private fun initView() {
        rv_list = findViewById(R.id.rv_list)

        rv_list?.apply {
            mAdapter = RvAdapterV2(dataList())

            layoutManager = LinearLayoutManager(this.context)
            adapter = mAdapter
        }
    }


    fun dataList(): List<ItemTitle> {
        val mList: MutableList<ItemTitle> = mutableListOf()
        val sysTime: Long = System.currentTimeMillis()
        val random: Random = Random()

        for (i in 0..19) {
            mList.add(ItemTitle("标题 > $i", sysTime + (random.nextInt(100) * 3000)))
        }

        return mList;
    }


}