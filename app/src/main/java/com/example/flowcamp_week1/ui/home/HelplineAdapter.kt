package com.example.flowcamp_week1.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.Transliterator.Position
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.flowcamp_week1.R
import com.example.flowcamp_week1.databinding.HelplineLayoutBinding
import org.w3c.dom.Text

class HelplineAdapter(private val itemList: List<HelplineModel>): RecyclerView.Adapter<HelplineAdapter.HelplineViewHolder>() {

    class HelplineViewHolder(private val binding: HelplineLayoutBinding): RecyclerView.ViewHolder(binding.root){

        //데이터 바인딩 메서드
        @SuppressLint("SetTextI18n")
        fun bind(helpline: HelplineModel){
            //1) 이미지, 타이틀, 연락처, bullet list 설정
            binding.helplineImage.setImageResource(helpline.imageResId)
            binding.helplineTitle.text = helpline.title
            binding.helplineContact.text = helpline.contact

            //bullet list 동적으로 생성
            binding.helplineBulletList.removeAllViews()
            helpline.bulletList.forEach{bullet ->
                val bulletTextView = TextView(binding.root.context).apply {
                    text = "\u2022 $bullet"
                    textSize = 14f
                    setPadding(0, 4, 0, 4)
                }
                binding.helplineBulletList.addView(bulletTextView)
            }

            //2) 아이템 클릭 리스터 (전화 걸기 팝업)
            binding.root.setOnClickListener{
                //내가 정의한 팝업 표시
                showCustomDialog(helpline.contact)
            }
        }

        private fun showCustomDialog(contact: String) {
            //Customdialog view 생성
            val dialogView = LayoutInflater.from(binding.root.context).inflate(R.layout.custom_dialog, null)

            //Dialog 생성
            val dialog = AlertDialog.Builder(binding.root.context).create()
            dialog.setView(dialogView)

            //Dialog view에서 Textview 요소 참조
            val title = dialogView.findViewById<TextView>(R.id.dialog_title)
            val message = dialogView.findViewById<TextView>(R.id.dialog_contact)
            val confirmButton = dialogView.findViewById<TextView>(R.id.dialog_confirm)
            val cancelButton = dialogView.findViewById<TextView>(R.id.dialog_cancel)

            //메시지 설정
            message.text = "$contact"

            //확인 버튼 클릭 시 동작
            confirmButton.setOnClickListener{
                //전화 앱 실행
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    //전화번호 문자열에서 공백, 특수문자 제거
                    val cleanNumber = contact.replace("[^0-9]".toRegex(), "")
                    data = Uri.parse("tel: $cleanNumber")
                }
                binding.root.context.startActivity(intent)
                dialog.dismiss()
            }

            //취소버튼 클릭 시 Dialog 닫기
             cancelButton.setOnClickListener{
                 dialog.dismiss()
             }

            //Dialog 표시 (??)
            dialog.show()
        }
/*
        private fun showDialPopup(contact: String) {
            AlertDialog.Builder(binding.root.context).apply {
                setTitle("전화 걸기")
                setMessage("이 번호로 전화 걸기 : $contact")

                //[확인] 버튼 클릭 시
                setPositiveButton("확인") { dialog, _ ->
                    //전화 앱 실행(ACTION_DIAL)
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        //전화번호 문자열에서 공백, 특수문자 제거(권장)
                        val cleanNumber = contact.replace("[^0-9]".toRegex(), "")
                        data = Uri.parse("tel: $cleanNumber")
                    }
                    binding.root.context.startActivity(intent)
                    dialog.dismiss()
                }

                //[취소] 버튼 클릭 시
                setNegativeButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }
            }.show()
        }*/
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