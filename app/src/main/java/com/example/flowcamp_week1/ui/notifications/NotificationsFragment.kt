package com.example.flowcamp_week1.ui.notifications

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flowcamp_week1.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.CoroutineStart

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val exchangeRate = 0.00075

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        setupListeners()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        // 원 입력 필드의 변경 감지
        binding.wonInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?){
                if (binding.wonInput.isFocused){
                    val wonValue = s.toString().toDoubleOrNull() ?: 0.0
                    val dollarValue = wonValue * exchangeRate
                    binding.dollarInput.setText(String.format("%.2f", dollarValue))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int){}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 달러 입력 필드의 변경 감지
        binding.dollarInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.dollarInput.isFocused){
                    if (binding.dollarInput.isFocused){
                        val dollarValue = s.toString().toDoubleOrNull() ?: 0.0
                        val wonValue = dollarValue / exchangeRate
                        binding.wonInput.setText(String.format("%.0f", wonValue))
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}