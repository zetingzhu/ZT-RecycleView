package com.example.zt_rvhor_marquee.pubu

/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.layout.layout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.zt_rvhor_marquee.R
import java.util.Random

class PoemWaterfallActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var poemAdapter: PoemStaggeredAdapter
    private val poemsList = mutableListOf<Poem>()
    private val random = Random()
    private val predefinedColors = listOf(
        Color.parseColor("#E1BEE7"), Color.parseColor("#D1C4E9"), Color.parseColor("#C5CAE9"),
        Color.parseColor("#BBDEFB"), Color.parseColor("#B3E5FC"), Color.parseColor("#B2EBF2"),
        Color.parseColor("#B2DFDB"), Color.parseColor("#C8E6C9"), Color.parseColor("#DCEDC8"),
        Color.parseColor("#F0F4C3"), Color.parseColor("#FFF9C4"), Color.parseColor("#FFECB3"),
        Color.parseColor("#FFE0B2"), Color.parseColor("#FFCCBC")
    )

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PoemWaterfallActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_poem_waterfall)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.rv_poems_waterfall)

        setupRecyclerView()
        loadPoems()
    }

    private fun setupRecyclerView() {
        // StaggeredGridLayoutManager 参数: spanCount (列数), orientation (方向)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        poemAdapter = PoemStaggeredAdapter(poemsList)
        recyclerView.adapter = poemAdapter
    }

    private fun loadPoems() {
        // 示例古诗数据 - 你可以从文件、网络或数据库加载更丰富的古诗
        val samplePoems = listOf(
            Poem(
                "赋得古原草送别",
                "白居易",
                listOf(
                    "离离原上草，",
                    "一岁一枯荣。",
                    "野火烧不尽，",
                    "春风吹又生。",
                    "远芳侵古道，",
                    "晴翠接荒城。",
                    "又送王孙去，",
                    "萋萋满别情。"
                )
            ),
            Poem(
                "静夜思",
                "李白",
                listOf("床前明月光，", "疑是地上霜。", "举头望明月，", "低头思故乡。")
            ),
            Poem(
                "春晓",
                "孟浩然",
                listOf("春眠不觉晓，", "处处闻啼鸟。", "夜来风雨声，", "花落知多少。")
            ),
            Poem(
                "登鹳雀楼",
                "王之涣",
                listOf("白日依山尽，", "黄河入海流。", "欲穷千里目，", "更上一层楼。"),
                getRandomLightColor()
            ),
            Poem(
                "相思",
                "王维",
                listOf("红豆生南国，", "春来发几枝。", "愿君多采撷，", "此物最相思。")
            ),
            Poem(
                "咏鹅",
                "骆宾王",
                listOf("鹅，鹅，鹅，", "曲项向天歌。", "白毛浮绿水，", "红掌拨清波。"),
                getRandomLightColor()
            ),
            Poem(
                "悯农（其一）",
                "李绅",
                listOf("春种一粒粟，", "秋收万颗子。", "四海无闲田，", "农夫犹饿死。")
            ),
            Poem(
                "游子吟",
                "孟郊",
                listOf(
                    "慈母手中线，",
                    "游子身上衣。",
                    "临行密密缝，",
                    "意恐迟迟归。",
                    "谁言寸草心，",
                    "报得三春晖。"
                ),
                getRandomLightColor()
            ),
            Poem(
                "寻隐者不遇",
                "贾岛",
                listOf("松下问童子，", "言师采药去。", "只在此山中，", "云深不知处。"),
                getRandomLightColor()
            ),
            Poem(
                "题西林壁",
                "苏轼",
                listOf("横看成岭侧成峰，", "远近高低各不同。", "不识庐山真面目，", "只缘身在此山中。")
            ),
            Poem(
                "望庐山瀑布",
                "李白",
                listOf("日照香炉生紫烟，", "遥看瀑布挂前川。", "飞流直下三千尺，", "疑是银河落九天。"),
                getRandomLightColor()
            ),
            Poem(
                "送元二使安西",
                "王维",
                listOf("渭城朝雨浥轻尘，", "客舍青青柳色新。", "劝君更尽一杯酒，", "西出阳关无故人。")
            ),
            Poem(
                "江南春",
                "杜牧",
                listOf("千里莺啼绿映红，", "水村山郭酒旗风。", "南朝四百八十寺，", "多少楼台烟雨中。"),
                getRandomLightColor()
            ),
            Poem(
                "早发白帝城",
                "李白",
                listOf("朝辞白帝彩云间，", "千里江陵一日还。", "两岸猿声啼不住，", "轻舟已过万重山。")
            ),
            Poem(
                "枫桥夜泊",
                "张继",
                listOf("月落乌啼霜满天，", "江枫渔火对愁眠。", "姑苏城外寒山寺，", "夜半钟声到客船。"),
                getRandomLightColor()
            )
        )

        // 为了瀑布流效果更明显，可以重复添加一些数据或随机生成不同长度的内容
        repeat(3) { // 重复添加3次，让列表更长
            poemsList.addAll(
                samplePoems.shuffled()
                    .map { it.copy(backgroundColor = getRandomLightColor()) }) // 打乱顺序并随机背景色
        }

        poemAdapter.notifyDataSetChanged()
    }

    private fun getRandomLightColor(): Int {
        return predefinedColors[random.nextInt(predefinedColors.size)]
    }
}
