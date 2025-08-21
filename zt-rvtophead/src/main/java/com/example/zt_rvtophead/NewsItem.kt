package com.example.zt_rvtophead

import java.io.Serializable

data class NewsItem(
    val title: String,
    val imageUrl: Int,
    val content: String,
    val desc: String
) : Serializable
