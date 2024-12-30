package com.example.flowcamp_week1.ui.dashboard

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flowcamp_week1.R
import com.example.flowcamp_week1.utils.tab2_data_tree
import java.io.File

class PhotoAdapter(
    private val photoList: List<tab2_data_tree>,
    private val onPhotoClick: (tab2_data_tree) -> Unit,
    private val onPhotoLongClick: (tab2_data_tree) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val extraInfoView: TextView = itemView.findViewById(R.id.textViewExtraInfo) // extrainfo 추가
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        val context = holder.itemView.context

        // Glide를 사용하여 이미지 로드
        Glide.with(context)
            .load(
                when {
                    photo.image.startsWith("content://") -> Uri.parse(photo.image) // URI 처리
                    photo.image.startsWith("/") -> File(photo.image) // 내부 저장소 파일 경로
                    else -> { // 리소스 이름 처리
                        val resId = context.resources.getIdentifier(photo.image, "drawable", context.packageName)
                        if (resId != 0) resId else null
                    }
                }
            )
            .override(300, 300) // 크기 제한
            .fitCenter() // 이미지 비율 유지
            .into(holder.imageView)

        // 텍스트 설정
        holder.textView.text = photo.description
        holder.extraInfoView.text = photo.extrainfo // extrainfo 표시

        if (photo.extrainfo == "여행유의") {
            holder.extraInfoView.setBackgroundResource(R.drawable.tab2_blue_border) // 남색 테두리
        }
        else if(photo.extrainfo=="여행안전") {
            holder.extraInfoView.setBackgroundResource(R.drawable.tab2_white_border)
        }
        else {
            holder.extraInfoView.setBackgroundResource(0) // 기본 배경
        }

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            onPhotoClick(photo) // 클릭된 항목 데이터 전달
        }

        // 꾹 누르기 이벤트 처리
        holder.itemView.setOnLongClickListener {
            if (photo.parent_id != 0) { // parent_id == 0인 주 항목은 삭제 불가
                onPhotoLongClick(photo)
            }
            true
        }
    }

    override fun getItemCount(): Int = photoList.size
}
