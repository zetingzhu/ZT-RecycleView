package com.example.zt_rvhor_marquee.pubu

/**
 * @author: zeting
 * @date: 2025/7/11
 *
 */

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_rvhor_marquee.R
import java.util.Random

// 假设 Poem 类如前定义

class PoemStaggeredAdapter(private val poems: List<Poem>) :
    RecyclerView.Adapter<PoemStaggeredAdapter.PoemViewHolder>() {

    private val random = Random()
    private val predefinedColors = listOf(
        Color.parseColor("#E1BEE7"), // Light Purple
        Color.parseColor("#D1C4E9"), // Deep Purple Light
        Color.parseColor("#C5CAE9"), // Indigo Light
        Color.parseColor("#BBDEFB"), // Blue Light
        Color.parseColor("#B3E5FC"), // Light Blue Light
        Color.parseColor("#B2EBF2"), // Cyan Light
        Color.parseColor("#B2DFDB"), // Teal Light
        Color.parseColor("#C8E6C9"), // Green Light
        Color.parseColor("#DCEDC8"), // Light Green Light
        Color.parseColor("#F0F4C3"), // Lime Light
        Color.parseColor("#FFF9C4"), // Yellow Light
        Color.parseColor("#FFECB3"), // Amber Light
        Color.parseColor("#FFE0B2"), // Orange Light
        Color.parseColor("#FFCCBC")  // Deep Orange Light
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_poem_staggered, parent, false)
        return PoemViewHolder(view)
    }

    override fun onBindViewHolder(holder: PoemViewHolder, position: Int) {
        val poem = poems[position]
        holder.bind(poem, poem.backgroundColor ?: getRandomLightColor())
    }

    override fun getItemCount(): Int = poems.size

    private fun getRandomLightColor(): Int {
        return predefinedColors[random.nextInt(predefinedColors.size)]
    }

    class PoemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.tv_poem_title)
        private val authorView: TextView = itemView.findViewById(R.id.tv_poem_author)
        private val contentView: TextView = itemView.findViewById(R.id.tv_poem_content)
        private val cardBackground: LinearLayout = itemView.findViewById(R.id.ll_poem_card_background) // 或者 CardView 本身

        fun bind(poem: Poem, backgroundColor: Int) {
            titleView.text = poem.title
            authorView.text = poem.author
            contentView.text = poem.contentLines.joinToString("\n") // 将诗句列表合并为带换行的字符串

            // itemView (CardView) 本身可能更适合设置背景色，或者内部的 LinearLayout
            (itemView as? CardView)?.setCardBackgroundColor(backgroundColor)
            // 或者 cardBackground.setBackgroundColor(backgroundColor)
        }
    }
}
