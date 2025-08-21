package com.example.zt_horandversliding.model

/**
 * 表格列定义
 * @param id 列ID
 * @param title 列标题
 * @param width 列宽度（单位：dp）
 */
data class TableColumn(
    val id: String,
    val title: String,
    val width: Int = 200
)

/**
 * 表格行数据
 * @param id 行ID
 * @param cells 单元格数据，key为列ID，value为单元格内容
 */
data class TableRow(
    val id: String,
    val cells: Map<String, String>
)