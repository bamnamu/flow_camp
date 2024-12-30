package com.example.flowcamp_week1.utils

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.flowcamp_week1.utils.tab2_data_tree

// RAW JSON 파일을 내부 저장소로 복사
fun copyRawJsonToInternal(context: Context, rawResId: Int, fileName: String) {
    val inputStream = context.resources.openRawResource(rawResId)
    val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
}

// 내부 저장소에서 JSON 파일 읽기
fun Context.readJsonFile(fileName: String): String {
    return openFileInput(fileName).bufferedReader().use { it.readText() }
}

// 내부 저장소에 JSON 파일 저장
fun Context.saveJsonFile(fileName: String, data: List<tab2_data_tree>) {
    val jsonString = Json.encodeToString(data) // 데이터를 JSON 문자열로 변환
    openFileOutput(fileName, Context.MODE_PRIVATE).use {
        it.write(jsonString.toByteArray())
    }
}

// JSON 데이터에 새 항목 추가
fun addPhoto(context: Context, fileName: String, newPhoto: tab2_data_tree) {
    val jsonString = context.readJsonFile(fileName)
    val currentData = Json.decodeFromString<List<tab2_data_tree>>(jsonString)

    val updatedData = currentData + newPhoto // 새 항목 추가
    context.saveJsonFile(fileName, updatedData) // 저장
}

// JSON 데이터에서 항목 삭제
fun deletePhoto(context: Context, fileName: String, photoId: Int) {
    val jsonString = context.readJsonFile(fileName)
    val currentData = Json.decodeFromString<List<tab2_data_tree>>(jsonString)

    val updatedData = currentData.filter { it.id != photoId } // 특정 ID의 항목 삭제
    context.saveJsonFile(fileName, updatedData) // 저장
}

// 특정 parent_id의 데이터를 필터링
fun getChildrenByParentId(context: Context, fileName: String, parentId: Int): List<tab2_data_tree> {
    val jsonString = context.readJsonFile(fileName)
    val currentData = Json.decodeFromString<List<tab2_data_tree>>(jsonString)

    return currentData.filter { it.parent_id == parentId } // parent_id로 필터링
}
