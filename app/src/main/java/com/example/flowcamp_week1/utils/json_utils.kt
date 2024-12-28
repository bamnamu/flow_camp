package com.example.flowcamp_week1.utils

import android.content.Context
import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
import com.example.flowcamp_week1.R
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken

// 데이터 클래스
data class tab2_data_tree(
    val image: String,
    val description: String,
    val children: List<tab2_data_tree> = emptyList() // 기본값 설정
)

// JSON 데이터를 읽어오는 함수
fun loadPhotoData(context: Context): List<tab2_data_tree> {
    // JSON 파일 읽기
    val inputStream = context.resources.openRawResource(R.raw.tab2_photo_states)
    val json = inputStream.bufferedReader().use { it.readText() }

    // JSON 데이터를 파싱하여 반환
    val gson = Gson()
    val type = object : TypeToken<List<tab2_data_tree>>() {}.type
    return gson.fromJson(json, type)
}
