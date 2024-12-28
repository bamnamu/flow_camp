package com.example.flowcamp_week1.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.flowcamp_week1.ui.home.HelplineModel
import com.example.flowcamp_week1.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

//json을 파싱할 중간 모델 (HelplineModel과 별개로 json 속 필드를 매핑할 간단한 클래스 필요)
data class HelplineJsonData(
    val imageResId: String,
    val title: String,
    val bulletList: List<String>,
    val contact: String
)

@SuppressLint("DiscouragedApi")
fun loadHelplineData(context: Context): List<HelplineModel>{
    //raw 폴더에 있는 helpline.json 열기
    val inputStream = context.resources.openRawResource(R.raw.helpline)
    val jsonString = inputStream.bufferedReader().use {it.readText()}

    //json -> List<HelplineJsonData> 변환
    val gson = Gson()
    val type = object : TypeToken<List<HelplineJsonData>>() {}.type
    val jsonDataList: List<HelplineJsonData> = gson.fromJson(jsonString, type)

    //imageName -> 실제 리소스 ID 변환 & HelplineModel 생성
    return jsonDataList.map { item ->
        val imageResId = context.resources.getIdentifier(
            item.imageResId,
            "drawable",
            context.packageName
        )
        HelplineModel(
            imageResId = imageResId,
            title = item.title,
            bulletList = item.bulletList,
            contact = item.contact
        )
    }
}