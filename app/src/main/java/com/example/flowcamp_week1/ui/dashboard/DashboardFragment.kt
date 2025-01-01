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
    private var currentParentId: Int = 0 // 현재 부모 ID를 저장

    private var isMultiSelectMode = false
    private val selectedItems = mutableSetOf<Int>()

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
            if (isMultiSelectMode) {
                disableMultiSelectMode()
            } else if (parentDataStack.isNotEmpty()) {
                currentPhotoData = parentDataStack.removeAt(parentDataStack.size - 1)
                currentParentId = if (currentPhotoData.isNotEmpty()) currentPhotoData[0].parent_id else 0
                showPhotoData(currentPhotoData)
            }
        }

        // 추가 버튼 클릭 이벤트
        binding.addButton.setOnClickListener {
            showAddPhotoDialog()
        }

        // 삭제 버튼 클릭 이벤트
        binding.deleteSelectedButton.setOnClickListener {
            showDeleteSelectedDialog()
        }

        // 공유 버튼 클릭 이벤트
        binding.shareSelectedButton.setOnClickListener {
            shareSelectedItems()
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
        currentPhotoData = photoData // 현재 데이터 업데이트
        currentParentId = if (photoData.isNotEmpty()) {
            if (photoData[0].parent_id == 0) photoData[0].id else photoData[0].parent_id
        } else {
            currentParentId // 최상위 레벨
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = PhotoAdapter(photoData,
            onPhotoClick = { clickedItem ->
                if (!isMultiSelectMode) {
                    if (clickedItem.parent_id == 0) {
                        val children = allPhotoData.filter { it.parent_id == clickedItem.id }
                        parentDataStack.add(currentPhotoData) // 현재 데이터 스택에 저장
                        currentPhotoData = children
                        if (clickedItem.id != 0) {
                            currentParentId = clickedItem.id // 부모 ID 설정
                        }
                        Log.d("Database", "설정 : $currentParentId")
                        showPhotoData(children) // 자식 항목 표시
                    } else {
                        showMapDialog(clickedItem) // 관광지 클릭 시 지도 연결
                    }
                } else {
                    toggleItemSelection(clickedItem.id)
                }
            },
            onPhotoLongClick = { enableMultiSelectMode()
                Log.d("DashboardFragment", "롱클릭 이벤트 발생, 다중 선택 모드 활성화")},
            isMultiSelectMode = isMultiSelectMode,
            selectedItems = selectedItems,
            onItemSelected = { id, isSelected ->
                if (isSelected) selectedItems.add(id) else selectedItems.remove(id)
            }
        )

        // 뒤로 가기 및 추가 버튼 가시성 업데이트
        val isBackButtonVisible = parentDataStack.isNotEmpty()
        binding.backButton.visibility = if (isBackButtonVisible || isMultiSelectMode) View.VISIBLE else View.GONE
        binding.addButton.visibility = if (!isMultiSelectMode&&isBackButtonVisible) View.VISIBLE else View.GONE
        binding.actionButtons.visibility = if (isMultiSelectMode) View.VISIBLE else View.GONE
        Log.d("DashboardFragment", "$isMultiSelectMode")
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

        selectedImageView = imageView

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
                        image = imagePath,
                        description = description.ifBlank { "설명을 입력하세요."},
                        parent_id = currentParentId, // 현재 부모 ID 사용
                        extrainfo = extraInfo.ifBlank { "주소를 입력하세요." }
                    )
                    Log.d("Database", "parent id : $currentParentId")
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

    private fun showDeleteSelectedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제하시겠습니까?")
            .setMessage("선택한 항목을 삭제합니다.")
            .setPositiveButton("삭제") { dialog, _ ->
                deleteSelectedItems()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun deleteSelectedItems() {
        val itemsToDelete = allPhotoData.filter { it.id in selectedItems }
        allPhotoData = allPhotoData - itemsToDelete
        savePhotoDataToInternal(allPhotoData)
        currentPhotoData = currentPhotoData.filter { it.id !in selectedItems }
        disableMultiSelectMode()
        showPhotoData(currentPhotoData)
    }

    private fun shareSelectedItems() {
        val sharedText = selectedItems.joinToString("\n") { id ->
            val item = allPhotoData.find { it.id == id }
            "${item?.description} - ${item?.extrainfo}"
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sharedText)
        }
        startActivity(Intent.createChooser(shareIntent, "공유하기"))
    }

    private fun enableMultiSelectMode() {
        isMultiSelectMode = true
        selectedItems.clear()
        binding.recyclerView.adapter?.notifyDataSetChanged()
        showPhotoData(currentPhotoData)
    }

    private fun disableMultiSelectMode() {
        isMultiSelectMode = false
        selectedItems.clear()
        binding.recyclerView.adapter?.notifyDataSetChanged()
        showPhotoData(currentPhotoData)
    }

    private fun toggleItemSelection(itemId: Int) {
        if (selectedItems.contains(itemId)) {
            selectedItems.remove(itemId)
        } else {
            selectedItems.add(itemId)
        }
        showPhotoData(currentPhotoData)
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
