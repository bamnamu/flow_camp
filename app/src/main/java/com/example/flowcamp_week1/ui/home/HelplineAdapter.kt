package com.example.flowcamp_week1.ui.home

import android.annotation.SuppressLint
import android.icu.text.Transliterator.Position
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flowcamp_week1.databinding.HelplineLayoutBinding

class HelplineAdapter(private val itemList: List<HelplineModel>): RecyclerView.Adapter<HelplineAdapter.HelplineViewHolder>() {

    class HelplineViewHolder(private val binding: HelplineLayoutBinding): RecyclerView.ViewHolder(binding.root){

        //데이터 바인딩 메서드
        @SuppressLint("SetTextI18n")
        fun bind(helpline: HelplineModel){
            binding.helplineImage.setImageResource(helpline.imageResId)
            binding.helplineTitle.text = helpline.title
            binding.helplineContact.text = helpline.contact

            //bullet list 처리
            binding.helplineBulletList.removeAllViews()
            helpline.bulletList.forEach{bullet ->
                val bulletTextView = TextView(binding.root.context).apply {
                    text = "\u2022 $bullet"
                    textSize = 14f
                    setPadding(0, 4, 0, 4)
                }
                binding.helplineBulletList.addView(bulletTextView)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelplineViewHolder {
        val binding = HelplineLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HelplineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HelplineViewHolder, position: Int){
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

}