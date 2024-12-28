package com.example.flowcamp_week1.ui.dashboard

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowcamp_week1.R
import com.example.flowcamp_week1.PhotoItem
import com.example.flowcamp_week1.utils.tab2_data_tree

class PhotoAdapter(private val photoList: List<tab2_data_tree>,
    private val onPhotoClick: (List<tab2_data_tree>) -> Unit) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

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

        // 이미지와 텍스트 설정
        val imageResId = context.resources.getIdentifier(photo.image, "drawable", context.packageName)
        holder.imageView.setImageResource(imageResId)
        holder.textView.text = photo.description

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener {
            if (photo.children.isNotEmpty()) {
                onPhotoClick(photo.children) // 하위 데이터 전달
            }
        }
    }


    override fun getItemCount(): Int = photoList.size

    private fun showDetailDialog(view: View, photo: tab2_data_tree) {
        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.tab2_photo_detail, null)
        val imageView: ImageView = dialogView.findViewById(R.id.dialog_image)
        val textView: TextView = dialogView.findViewById(R.id.dialog_description)

        // 이미지 및 텍스트 설정
        val context = view.context
        val imageResId = context.resources.getIdentifier(photo.image, "drawable", context.packageName)
        imageView.setImageResource(imageResId)
        textView.text = photo.description

        // 다이얼로그 생성
        AlertDialog.Builder(view.context)
            .setView(dialogView)
            .setPositiveButton("닫기") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
