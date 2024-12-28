package com.example.flowcamp_week1.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flowcamp_week1.databinding.FragmentDashboardBinding
import com.example.flowcamp_week1.utils.loadPhotoData
import com.example.flowcamp_week1.utils.tab2_data_tree

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var currentPhotoData: List<tab2_data_tree> = emptyList()

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

        // JSON 데이터 로드
        val allPhotoData = loadPhotoData(requireContext()) // JSON 데이터를 로드
        currentPhotoData = allPhotoData // 최상위 데이터로 초기화

        // RecyclerView 초기화
        setupRecyclerView(currentPhotoData)
    }

    private fun setupRecyclerView(photoData: List<tab2_data_tree>) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // RecyclerView 어댑터 설정
        recyclerView.adapter = PhotoAdapter(photoData) { children ->
            if (children.isNotEmpty()) {
                currentPhotoData = children
                setupRecyclerView(children)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
