package com.example.flowcamp_week1.ui.dashboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flowcamp_week1.R
import com.example.flowcamp_week1.databinding.FragmentDashboardBinding
import com.example.flowcamp_week1.utils.loadPhotoData
import com.example.flowcamp_week1.utils.tab2_data_tree
import androidx.activity.result.contract.ActivityResultContracts

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var currentPhotoData: List<tab2_data_tree> = emptyList()
    private val parentDataStack = mutableListOf<List<tab2_data_tree>>()

    // 갤러리에서 선택한 이미지 URI 저장 및 ImageView 참조
    private var selectedImageUri: Uri? = null
    private var selectedImageView: ImageView? = null

    // ActivityResultLauncher 등록 (갤러리에서 이미지 선택)
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data // 선택한 이미지 URI 저장
            selectedImageView?.setImageURI(selectedImageUri) // Dialog의 ImageView에 선택한 이미지 표시
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 데이터 로드
        val allPhotoData = loadPhotoData(requireContext())
        showPhotoData(allPhotoData)

        // 뒤로 가기 버튼 클릭 이벤트
        binding.backButton.setOnClickListener {
            if (parentDataStack.isNotEmpty()) {
                currentPhotoData = parentDataStack.removeAt(parentDataStack.size - 1)
                showPhotoData(currentPhotoData)
            }
        }

        // 추가 버튼 클릭 이벤트
        binding.addButton.setOnClickListener {
            showAddPhotoDialog()
        }
    }

    private fun showPhotoData(photoData: List<tab2_data_tree>) {
        currentPhotoData = photoData
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = PhotoAdapter(photoData) { children ->
            if (children.isNotEmpty()) {
                parentDataStack.add(currentPhotoData)
                showPhotoData(children)
            }
        }

        // 뒤로 가기 및 추가 버튼 가시성 업데이트
        val isBackButtonVisible = parentDataStack.isNotEmpty()
        binding.backButton.visibility = if (isBackButtonVisible) View.VISIBLE else View.GONE
        binding.addButton.visibility = if (isBackButtonVisible) View.VISIBLE else View.GONE
    }

    private fun showAddPhotoDialog() {
        // Dialog 레이아웃 Inflate
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_photo, null)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val imageView = dialogView.findViewById<ImageView>(R.id.imageViewSelected)

        selectedImageView = imageView // 선택한 ImageView 참조 저장

        // 이미지 클릭 이벤트 (갤러리 열기)
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        // Dialog 생성
        AlertDialog.Builder(requireContext())
            .setTitle("사진 추가")
            .setView(dialogView)
            .setPositiveButton("완료") { dialog, _ ->
                val description = descriptionEditText.text.toString()

                if (selectedImageUri != null && description.isNotBlank()) {
                    // 새로운 데이터 추가
                    val newPhoto = tab2_data_tree(
                        image = selectedImageUri.toString(),
                        description = description,
                        children = emptyList()
                    )
                    val updatedList = currentPhotoData + newPhoto
                    showPhotoData(updatedList) // RecyclerView 업데이트
                } else {
                    Toast.makeText(requireContext(), "이미지와 설명을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
