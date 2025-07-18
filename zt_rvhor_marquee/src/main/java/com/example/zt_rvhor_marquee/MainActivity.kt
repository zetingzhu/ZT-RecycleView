package com.example.zt_rvhor_marquee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zt_rvhor_marquee.blibli.BliBliDanmuAct
import com.example.zt_rvhor_marquee.danmu.DanmuAct
import com.example.zt_rvhor_marquee.danmurv.LaneTestAct
import com.example.zt_rvhor_marquee.hor.YourHorActivity
import com.example.zt_rvhor_marquee.pubu.PoemWaterfallActivity
import com.example.zt_rvhor_marquee.test.TestMarqueeAct
import com.example.zt_rvhor_marquee.ui.theme.ZTRecycleViewTheme
import com.example.zt_rvhor_marquee.ver.YourVerActivity
import com.example.zt_rvhor_marquee.ver_ai.AiYourVerActivity
import com.example.zt_rvhor_marquee.vers.VerSingleAct

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZTRecycleViewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val current = LocalContext.current
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(onClick = {
            TestMarqueeAct.start(current)
        }) {
            Text(text = "测试跑马灯效果")
        }

        Spacer(modifier = Modifier.padding(10.dp))
        HorizontalDivider(thickness = 2.dp, color = Color(0xff3367ba))
        Spacer(modifier = Modifier.padding(10.dp))

        Button(onClick = {
            YourHorActivity.start(current)
        }) {
            Text(text = "AI 用 RecycleView 实现横向跑马灯效果")
        }
        Button(onClick = {
            AiYourVerActivity.start(current)
        }) {
            Text(text = "AI 用 RecycleView 实现垂直跑马灯效果")
        }

        Button(onClick = {
            YourVerActivity.start(current)
        }) {
            Text(text = "用 RecycleView 实现垂直跑马灯效果")
        }

        Button(onClick = {
            VerSingleAct.start(current)
        }) {
            Text(text = "垂直，当行，滚动跑马灯")
        }

        Button(onClick = {
            PoemWaterfallActivity.start(current)
        }) {
            Text(text = "瀑布流效果")
        }

        Button(onClick = {
            DanmuAct.start(current)
        }) {
            Text(text = "横向弹幕效果 StaggeredGridLayoutManager （效果不满意）")
        }

        Button(onClick = {
            LaneTestAct.start(current)
        }) {
            Text(text = "自定义 LayoutManager 实现横向弹幕效果,只需要横向部分 （效果不满意）")
        }

        Button(onClick = {
            BliBliDanmuAct.start(current)
        }) {
            Text(text = "使用 bilibili 弹幕 sdk 实现横向弹幕效果")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ZTRecycleViewTheme {
        Greeting("Android")
    }
}