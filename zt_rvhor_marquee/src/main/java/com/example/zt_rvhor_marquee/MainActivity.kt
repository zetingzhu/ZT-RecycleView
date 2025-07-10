package com.example.zt_rvhor_marquee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.zt_rvhor_marquee.hor.YourHorActivity
import com.example.zt_rvhor_marquee.test.TestMarqueeAct
import com.example.zt_rvhor_marquee.ui.theme.ZTRecycleViewTheme
import com.example.zt_rvhor_marquee.ver.YourVerActivity
import com.example.zt_rvhor_marquee.ver_ai.AiYourVerActivity

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
            TestMarqueeAct.start(current)
        }) {
            Text(text = "测试跑马灯效果")
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