package com.example.flowcamp_week1.ui.dashboard

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photoList[position]
        val context = holder.itemView.context

        // 이미지 설정
        when {
            photo.image.startsWith("content://") -> { // URI 처리
                holder.imageView.setImageURI(Uri.parse(photo.image))
            }
            photo.image.startsWith("/") -> { // 내부 저장소 파일 경로 처리
                val file = File(photo.image)
                if (file.exists()) {
                    holder.imageView.setImageURI(Uri.fromFile(file))
                }
            }
            else -> { // 리소스 이름 처리
                val imageResId = context.resources.getIdentifier(photo.image, "drawable", context.packageName)
                if (imageResId != 0) {
                    holder.imageView.setImageResource(imageResId)
                }
            }
        }

        // 텍스트 설정
        holder.textView.text = photo.description

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
