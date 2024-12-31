package com.example.flowcamp_week1.ui.notifications

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.flowcamp_week1.databinding.FragmentNotificationsBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.withStateAtLeast
import com.example.flowcamp_week1.R
import com.example.flowcamp_week1.databinding.UtilitiesLengthLayoutBinding
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.min


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var exchangeRate = 1000 //임시. API로 가져와야 함. 1달러당 N원.

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("onCreateView", "just start")


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root = binding.root

        getExchangeRate()
        setupListeners()
        binding.exchangeRateDisplay.text = "1달러 당 $exchangeRate 원"

        // 길이 변환 누르면 나타나도록
        val lenView = LayoutInflater.from(binding.root.context).inflate(R.layout.utilities_length_layout, null)
        val lenDialog = AlertDialog.Builder(binding.root.context).create()
        lenDialog.setView(lenView)

        val lenButton = binding.conversionCardLength
        lenButton.setOnClickListener{
            lenDialog.show()
            setupLengthListeners()
        }

        return root
    }


    private fun setupListeners() {
        // 원 입력 필드의 변경 감지
        binding.wonInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?){
                if (binding.wonInput.isFocused){
                    val wonValue = s.toString().toDoubleOrNull() ?: 0.0
                    val dollarValue = wonValue / exchangeRate
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
                    val dollarValue = s.toString().toDoubleOrNull() ?: 0.0
                    val wonValue = dollarValue * exchangeRate
                    binding.wonInput.setText(String.format("%.0f", wonValue))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupLengthListeners() {
        val lengthView = LayoutInflater.from(binding.root.context).inflate(R.layout.utilities_length_layout, null)

        // 새로운 AlertDialog를 생성하여 Length Conversion Layout 표시
        val lengthDialog = AlertDialog.Builder(binding.root.context).create()
        lengthDialog.setView(lengthView)

        // lengthDialog 표시
        lengthDialog.show()

        // Length Conversion Layout의 뷰와 상호작용 예시
        val mInput = lengthView.findViewById<EditText>(R.id.m_input)
        val cmInput = lengthView.findViewById<EditText>(R.id.cm_input)
        val ftInput = lengthView.findViewById<EditText>(R.id.ft_input)
        val inInput = lengthView.findViewById<EditText>(R.id.in_input)

        //cm 필드의 변경 감지
        cmInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (cmInput.isFocused){
                    val cmValue = s.toString().toDoubleOrNull() ?:0.0
                    val mValue = cmValue / 100.0
                    val inValue = cmValue / 2.54
                    val ftValue = inValue / 12.0

                    //cm, m 모두 표시
                    mInput.setText(String.format("%.2f", mValue))
                    //ft, in 모두 표시
                    ftInput.setText(String.format("%.2f", ftValue))
                    inInput.setText(String.format("%.2f", inValue))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //m 변경 감지
        mInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (mInput.isFocused){
                    val mValue = s.toString().toDoubleOrNull() ?:0.0

                    val cmValue = mValue * 100.0
                    val inValue = cmValue / 2.54
                    val ftValue = inValue / 12.0

                    //cm, m 모두 표시
                    cmInput.setText(String.format("%.2f", cmValue))
                    //ft, in 모두 표시
                    ftInput.setText(String.format("%.2f", ftValue))
                    inInput.setText(String.format("%.2f", inValue))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        // in 변경 감지
        inInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (inInput.isFocused) {
                    val inValue = s.toString().toDoubleOrNull() ?: 0.0

                    val cmValue = inValue * 2.54
                    val mValue = cmValue / 100.0
                    val ftValue = inValue / 12.0

                    //cm, m 모두 표시
                    cmInput.setText(String.format("%.2f", cmValue))
                    mInput.setText(String.format("%.2f", mValue))
                    //ft 표시
                    ftInput.setText(String.format("%.2f", ftValue))
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //ft 변경 감지
        ftInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (ftInput.isFocused){
                    val ftValue = s.toString().toDoubleOrNull() ?:0.0

                    val inValue = ftValue * 12.0
                    val cmValue = inValue * 2.54
                    val mValue = cmValue / 100.0

                    cmInput.setText(String.format("%.2f", cmValue))
                    mInput.setText(String.format("%.2f", mValue))
                    inInput.setText(String.format("%.2f", inValue))
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    //환율 가져오는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    fun getExchangeRate(){
        Log.d("getExchangeRate", "getExchangeRate function started")

        val authkey = "GkWSnQO1HtS8mCK47jMqVaSra3htcuXj"
        Log.d("getExchangeRate", authkey)
        val searchDate = LocalDate.now().toString().replace("-", "")
        Log.d("getExchangeRate", searchDate)
        val data = "AP01"
        Log.d("getExchangeRate", data)
        //요청 보내기
        val reqURL =
            "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=$authkey&searchdate=$searchDate&data=$data"

        Log.d("getExchangeRate", reqURL) //이것까지 나옴

        try {
            val url = URL(reqURL)
            Log.d("getExchangeRate", "url : $url.toString()") //여기까지도 나오는 듯?

            val netConn = url.openConnection() as HttpURLConnection

            if (netConn.responseCode == HttpURLConnection.HTTP_OK){
                val reader = BufferedReader(InputStreamReader(netConn.inputStream))
                val response = StringBuilder()
                Log.d("getExchangeRate", "response: $response")
            }

/*
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000 // 연결 타임아웃
            connection.readTimeout = 5000 // 읽기 타임아웃

            //응답 코드 확인
            val responseCode = connection.responseCode
            Log.d("getExchangeRate", "responseCode: $responseCode")
            if (responseCode == HttpURLConnection.HTTP_OK){
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                //JSON 문자열 출력
                val jsonString = response.toString()
                Log.d("getExchangeRate", "API 응답: $jsonString")

                //필요한 값 추출(미국 환율)
                exchangeRate = extractDealBasR(jsonString, "USD")!!.toInt()
                Log.d("getExchangeRate", "ExchangeRate : $exchangeRate")

                //UI 업데이트
                requireActivity().runOnUiThread{
                    binding.exchangeRateDisplay.text = "1달러 당 $exchangeRate 원"
                }
            } else {
                println("HTTP 요청 실패")
                Log.d("getExchangeRate", "HTTP 요청 실패")
            }

            //연결 종료
            connection.disconnect()
            */

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun extractDealBasR(jsonString: String, currency: String): String? {
        val jsonArr = org.json.JSONArray(jsonString)
        for (i in 0 until jsonArr.length()){
            val jsonObject = jsonArr.getJSONObject(i)
            if (jsonObject.getString("cur_unit") == currency){
                return jsonObject.getString("deal_bas_r")
            }
        }
        return null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}