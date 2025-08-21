package com.example.zt_rvtophead

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderItemDecoration(
    private val getTitle: (position: Int) -> String
) : RecyclerView.ItemDecoration() {

    private val headerHeight = 120 // px，可根据需要调整
    private val headerPaint = Paint().apply {
        color = 0xFFF5F5F5.toInt()
    }
    private val textPaint = Paint().apply {
        color = 0xFF222222.toInt()
        textSize = 48f
        isAntiAlias = true
    }
    private val textPadding = 32f

    // 新增：Header点击回调接口
    interface OnHeaderClickListener {
        fun onHeaderClick(position: Int, title: String)
    }

    var headerClickListener: OnHeaderClickListener? = null

    // 判断触摸点是否在Header区域
    fun isInHeaderArea(x: Float, y: Float): Boolean {
        return y >= 0 && y <= headerHeight
    }

    // 提供Header高度
    fun getHeaderHeight(): Int = headerHeight

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        if (childCount == 0) return

        val firstView = parent.getChildAt(0)
        val firstPosition = parent.getChildAdapterPosition(firstView)
        val title = getTitle(firstPosition)

        // 尝试获取 itemView 内部的 rl_sticky_layout
        val rlStickyLayout = firstView.findViewById<View>(R.id.rl_sticky_layout)
        if (rlStickyLayout != null) {
            // 手动 measure/layout
            rlStickyLayout.measure(
                View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(headerHeight, View.MeasureSpec.EXACTLY)
            )
            rlStickyLayout.layout(0, 0, parent.width, headerHeight)

            // 判断下一个item是否需要推动header
            var offset = 0
            if (childCount > 1) {
                val secondView = parent.getChildAt(1)
                val secondPosition = parent.getChildAdapterPosition(secondView)
                if (getTitle(secondPosition) != title) {
                    val top = secondView.top
                    if (top < headerHeight) {
                        offset = top - headerHeight
                    }
                }
            }

            c.save()
            c.translate(0f, offset.toFloat())
            // 将 rl_sticky_layout 绘制到 Canvas
            rlStickyLayout.draw(c)
            c.restore()
            return
        }
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        // 只有非第一个item才设置顶部偏移
        if (position > 0) {
            outRect.top = 0
        } else {
            outRect.top = 0
        }
    }
}
