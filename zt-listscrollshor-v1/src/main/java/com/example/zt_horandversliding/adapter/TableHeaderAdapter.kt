package com.example.zt_horandversliding.adapter

import android.graphics.Paint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zt_horandversliding.R
import com.example.zt_horandversliding.model.TableColumn
import kotlin.math.max

/**
 * 表头适配器
 */
class TableHeaderAdapter(private val columns: List<TableColumn>) : 
    RecyclerView.Adapter<TableHeaderAdapter.HeaderViewHolder>() {
    
    // 存储计算后的列宽
    private val calculatedWidths = mutableMapOf<String, Int>()
    
    // 用于测量文本宽度的Paint对象
    private val paint = Paint()
    
    // 在适配器中添加一个属性来存储Resources.DisplayMetrics
    private var displayMetrics: android.util.DisplayMetrics? = null
    
    init {
        // 初始化时设置默认文本大小
        paint.textSize = 14f
        
        // 初始化时计算每列的宽度
        calculateColumnWidths()
    }
    
    /**
     * 设置屏幕密度，应在onCreateViewHolder中调用
     */
    private fun setDisplayMetrics(context: android.content.Context) {
        if (displayMetrics == null) {  // 只在第一次设置
            displayMetrics = context.resources.displayMetrics
            // 更新文本大小
            paint.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                14f,
                displayMetrics
            )
            // 重新计算列宽
            calculateColumnWidths()
        }
    }
    
    /**
     * 计算每列的宽度，基于标题文本长度
     */
    private fun calculateColumnWidths() {
        // 为每列计算宽度
        columns.forEach { column ->
            // 测量标题文本宽度
            val titleWidth = paint.measureText(column.title).toInt() + PADDING_HORIZONTAL * 2
            
            // 取标题宽度和默认宽度的较大值
            val width = max(titleWidth, column.width)
            calculatedWidths[column.id] = width
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        // 设置displayMetrics
        setDisplayMetrics(parent.context)
        
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table_header, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        val column = columns[position]
        holder.bind(column)
    }

    override fun getItemCount(): Int = columns.size

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_header_title)

        fun bind(column: TableColumn) {
            // 设置列宽为计算后的宽度
            val params = itemView.layoutParams
            params.width = calculatedWidths[column.id] ?: column.width
            itemView.layoutParams = params

            // 设置标题
            titleTextView.text = column.title
        }
    }

    /**
     * 获取计算后的列宽
     * @param columnId 列ID
     * @return 计算后的列宽
     */
    fun getCalculatedWidth(columnId: String): Int {
        return calculatedWidths[columnId] ?: 100
    }
    
    /**
     * 更新列宽度
     * @param columnId 列ID
     * @param width 新的宽度
     */
    fun updateColumnWidth(columnId: String, width: Int) {
        val oldWidth = calculatedWidths[columnId] ?: 0
        if (width > oldWidth) {
            calculatedWidths[columnId] = width
            notifyDataSetChanged()
        }
    }
    
    /**
     * 重新计算所有列宽
     * @param cellContents 所有单元格内容的映射，key为列ID，value为该列所有单元格的内容列表
     */
    fun recalculateWidths(cellContents: Map<String, List<String>>) {
        // 重新计算每列的宽度
        columns.forEach { column ->
            // 获取该列的所有单元格内容
            val contents = cellContents[column.id] ?: emptyList()
            
            // 计算所有单元格内容的最大宽度
            var maxWidth = column.width
            contents.forEach { content ->
                val contentWidth = paint.measureText(content).toInt() + PADDING_HORIZONTAL * 2
                maxWidth = max(maxWidth, contentWidth)
            }
            
            // 标题宽度也需要考虑
            val titleWidth = paint.measureText(column.title).toInt() + PADDING_HORIZONTAL * 2
            maxWidth = max(maxWidth, titleWidth)
            
            // 更新列宽
            calculatedWidths[column.id] = maxWidth
        }
        
        // 通知适配器数据已更改
        notifyDataSetChanged()
    }
    
    companion object {
        // 水平方向的内边距，用于文本宽度计算
        private const val PADDING_HORIZONTAL = 16
    }
}