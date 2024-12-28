package com.example.flowcamp_week1.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flowcamp_week1.databinding.FragmentHelplineBinding
import com.example.flowcamp_week1.utils.loadHelplineData

class HomeFragment : Fragment() {

    private var _binding: FragmentHelplineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHelplineBinding.inflate(inflater, container, false)
        val root: View = binding.root


        //실제 helpline 데이터 로드
        val helplineList = loadHelplineData(requireContext())

        //recyclerview 설정
        val recyclerView = binding.helplineRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HelplineAdapter(helplineList)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}