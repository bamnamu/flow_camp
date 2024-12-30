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
import com.example.flowcamp_week1.utils.*
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var allPhotoData: List<tab2_data_tree> = emptyList()
    private var currentPhotoData: List<tab2_data_tree> = emptyList()
    private val parentDataStack = mutableListOf<List<tab2_data_tree>>()
    private var selectedImageView: ImageView? = null
    private var selectedImageUri: Uri? = null
    private val fileName = "photos.json"

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageView?.setImageURI(selectedImageUri)
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

        // JSON 파일 초기화: 내부 저장소로 복사
        val file = requireContext().getFileStreamPath(fileName)
        if (!file.exists()) {
            copyRawJsonToInternal(requireContext(), R.raw.tab2_photo_states, fileName)
        }

        // 내부 저장소에서 JSON 파일 로드 및 데이터 처리
        allPhotoData = processPhotoData(requireContext(), fileName)
        currentPhotoData = allPhotoData.filter { it.parent_id == 0 } // parent_id가 0인 주만 표시
        showPhotoData(currentPhotoData)

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

    private fun loadPhotoDataFromInternal(): List<tab2_data_tree> {
        val jsonString = requireContext().readJsonFile(fileName)
        return Json.decodeFromString(jsonString)
    }

    private fun savePhotoDataToInternal(data: List<tab2_data_tree>) {
        requireContext().saveJsonFile(fileName, data)
    }

    private fun showPhotoData(photoData: List<tab2_data_tree>) {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = PhotoAdapter(photoData,
            onPhotoClick = { clickedItem ->
                if (clickedItem.parent_id != 0) {
                    // 관광지 클릭 시
                    showMapDialog(clickedItem)
                } else {
                    // 주 클릭 시
                    val children = allPhotoData.filter { it.parent_id == clickedItem.id }
                    if (clickedItem.parent_id==0) {
                        parentDataStack.add(currentPhotoData) // 현재 데이터 스택에 저장
                        currentPhotoData = children
                        showPhotoData(children) // 자식 항목 표시
                    }
                }
            },
            onPhotoLongClick = { clickedItem ->
                showDeleteDialog(clickedItem)
            }
        )

        // 뒤로 가기 및 추가 버튼 가시성 업데이트
        val isBackButtonVisible = parentDataStack.isNotEmpty()
        binding.backButton.visibility = if (isBackButtonVisible) View.VISIBLE else View.GONE
        binding.addButton.visibility = if (isBackButtonVisible) View.VISIBLE else View.GONE
    }

    private fun saveImageToInternalStorage(uri: Uri, fileName: String): String {
        val file = File(requireContext().filesDir, fileName) // 내부 저장소 파일 생성
        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream) // 이미지 저장
            }
        }
        return file.absolutePath // 저장된 파일 경로 반환
    }

    private fun showAddPhotoDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_photo, null)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription)
        val extraInfoEditText = dialogView.findViewById<EditText>(R.id.editTextExtraInfo) // extrainfo 입력 필드 추가
        val imageView = dialogView.findViewById<ImageView>(R.id.imageViewSelected)

        selectedImageView = imageView // 선택한 ImageView 참조 저장

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("사진 추가")
            .setView(dialogView)
            .setPositiveButton("완료") { dialog, _ ->
                val description = descriptionEditText.text.toString()
                val extraInfo = extraInfoEditText.text.toString()

                if (selectedImageUri != null && description.isNotBlank()) {
                    val fileName = "image_${System.currentTimeMillis()}.png"
                    val imagePath = saveImageToInternalStorage(selectedImageUri!!, fileName)

                    val newPhoto = tab2_data_tree(
                        id = allPhotoData.size + 1, // 고유 ID 생성
                        image = imagePath, // 내부 저장소 경로 저장
                        description = description,
                        parent_id = if (parentDataStack.isNotEmpty()) parentDataStack.last().first().id else 0,
                        extrainfo = extraInfo.ifBlank { if (parentDataStack.isEmpty()) "여행유의" else "주소를 입력하세요." }
                    )
                    allPhotoData = allPhotoData + newPhoto // 전체 데이터 업데이트
                    savePhotoDataToInternal(allPhotoData) // 내부 저장소에 저장
                    currentPhotoData = currentPhotoData + newPhoto // 현재 데이터 업데이트
                    showPhotoData(currentPhotoData) // RecyclerView 업데이트
                } else {
                    Toast.makeText(requireContext(), "이미지와 설명을 모두 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showMapDialog(photo: tab2_data_tree) {
        AlertDialog.Builder(requireContext())
            .setTitle("지도와 연결")
            .setMessage("이 관광지를 지도에서 확인하시겠습니까?")
            .setPositiveButton("연결") { _, _ ->
                openMap(photo.extrainfo)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun openMap(address: String) {
        try {
            val uri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "지도를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteDialog(photo: tab2_data_tree) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제하시겠습니까?")
            .setMessage("선택한 항목을 삭제합니다.")
            .setPositiveButton("삭제") { dialog, _ ->
                deletePhoto(photo)
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun deletePhoto(photo: tab2_data_tree) {
        allPhotoData = allPhotoData.filter { it.id != photo.id } // 해당 항목 삭제
        savePhotoDataToInternal(allPhotoData) // 내부 저장소 업데이트
        currentPhotoData = currentPhotoData.filter { it.id != photo.id } // 현재 화면 데이터 업데이트
        showPhotoData(currentPhotoData) // RecyclerView 업데이트
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
