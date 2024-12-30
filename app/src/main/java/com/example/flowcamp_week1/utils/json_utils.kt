package com.example.flowcamp_week1.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.flowcamp_week1.R
import kotlinx.serialization.Serializable

// 데이터 클래스
@Serializable
data class tab2_data_tree(
    val id: Int,
    val image: String,
    val description: String,
    val parent_id: Int
)

