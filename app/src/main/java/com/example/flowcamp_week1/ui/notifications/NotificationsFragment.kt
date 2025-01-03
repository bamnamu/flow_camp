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
import com.example.flowcamp_week1.utils.SimpleCookieJar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.net.ssl.*
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.net.ssl.*
import java.text.SimpleDateFormat
import java.util.Calendar



class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // 임시 환율값. API 결과로 업데이트됨.
    private var exchangeRate = 1000.0

    private var search_date=LocalDate.now()

    // OkHttpClient 인스턴스 생성 (SSL 검증 비활성화 포함)
    private val client: OkHttpClient by lazy { getUnsafeOkHttpClient() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("onCreateView", "just start")


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root = binding.root

        // 환율 호출
        fetchExchangeRate(search_date, 5)

        // EditText TextWatcher 설정
        setupTextWatchers()

        // 초기 환율 표시
        binding.exchangeRateDisplay.text = "1달러 당 $exchangeRate 원"

        // 1) length_layout inflate + AlertDialog 생성 (단 한 번)
        val lenView = LayoutInflater.from(binding.root.context).inflate(R.layout.utilities_length_layout, null)
        val lenDialog = AlertDialog.Builder(binding.root.context).create()
        lenDialog.setView(lenView)

        //2) View 참조
        val mInput = lenView.findViewById<EditText>(R.id.m_input)
        val cmInput = lenView.findViewById<EditText>(R.id.cm_input)
        val ftInput = lenView.findViewById<EditText>(R.id.ft_input)
        val inInput = lenView.findViewById<EditText>(R.id.in_input)

        //3) TextWatcher 설정
        //cm 변경 감지
        cmInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (cmInput.isFocused){
                    val cmValue = s.toString().toDoubleOrNull() ?:0.0
//                    val mValue = cmValue / 100.0
//                    val inValue = cmValue / 2.54
//                    val ftValue = inValue / 12.0
                    val mValue = mInput.text.toString().toDoubleOrNull() ?:0.0
                    var inValue = (cmValue + mValue * 100) / 2.54
                    val ftValue = (inValue/12).toInt()
                    inValue = inValue - ftValue * 12

                    //cm, m 모두 표시
                    mInput.setText(String.format("%d", mValue.toInt()))
                    //ft, in 모두 표시
                    ftInput.setText(String.format("%d", ftValue))
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
//
//                    val cmValue = mValue * 100.0
//                    val inValue = cmValue / 2.54
//                    val ftValue = inValue / 12.0
                    val cmValue = cmInput.toString().toDoubleOrNull() ?:0.0
                    var inValue = (cmValue + mValue * 100) / 2.54
                    val ftValue = (inValue/12).toInt()
                    inValue = inValue - ftValue * 12



                    //cm, m 모두 표시
                    cmInput.setText(String.format("%.2f", cmValue))
                    //ft, in 모두 표시
                    ftInput.setText(String.format("%d", ftValue))
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

//                    val cmValue = inValue * 2.54
//                    val mValue = cmValue / 100.0
//                    val ftValue = inValue / 12.0
                    val ftValue = ftInput.toString().toDoubleOrNull() ?:0.0
                    var cmValue = ftValue * 30.48 + inValue * 2.54
                    val mValue = (cmValue / 100).toInt()
                    cmValue = cmValue - mValue * 100

                    //cm, m 모두 표시
                    cmInput.setText(String.format("%.2f", cmValue))
                    mInput.setText(String.format("%d", mValue))
                    //ft 표시
                    ftInput.setText(String.format("%d", ftValue.toInt()))
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

//                    val inValue = ftValue * 12.0
//                    val cmValue = inValue * 2.54
//                    val mValue = cmValue / 100.0
                    val inValue = inInput.toString().toDoubleOrNull() ?:0.0
                    var cmValue = ftValue * 30.48 + inValue * 2.54
                    val mValue = (cmValue / 100).toInt()
                    cmValue = cmValue - mValue * 100

                    cmInput.setText(String.format("%.2f", cmValue))
                    mInput.setText(String.format("%d", mValue))
                    inInput.setText(String.format("%.2f", inValue))
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //4) 버튼 클릭 시 dialog.show()
        binding.conversionCardLength.setOnClickListener{
            lenDialog.show()
        }

//        val lenButton = binding.conversionCardLength
//        lenButton.setOnClickListener{
//            lenDialog.show()
//            setupLengthListeners()
//        }

        //1-1) length_layout inflate + AlertDialog 생성
        val weightView = LayoutInflater.from(binding.root.context).inflate(R.layout.utilities_weight_layout, null)
        val weightDialog = AlertDialog.Builder(binding.root.context).create()
        weightDialog.setView(weightView)

        //2-1) View 참조
        val kgInput = weightView.findViewById<EditText>(R.id.kg_input)
        val lbsInput = weightView.findViewById<EditText>(R.id.lbs_input)

        //3-1) TextWatcher 설정
        //kg 변경 감지
        kgInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (kgInput.isFocused){
                    val kgValue = s.toString().toDoubleOrNull() ?:0.0
                    val lbsValue = kgValue * 2.2

                    //lbs 표시
                    lbsInput.setText(String.format("%.2f", lbsValue))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        //lbs 변경 감지
        lbsInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (lbsInput.isFocused){
                    val lbsValue = s.toString().toDoubleOrNull() ?:0.0
                    val kgValue = lbsValue / 2.2

                    kgInput.setText(String.format("%.2f", kgValue))
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        //4-1) 버튼 클릭 시 dialog.show()
        binding.conversionCardWeight.setOnClickListener({
            weightDialog.show()
        })

        //1-2) length_layout inflate + AlertDialog 생성
        val tempView = LayoutInflater.from(binding.root.context).inflate(R.layout.utilities_temperature_layout, null)
        val tempDialog = AlertDialog.Builder(binding.root.context).create()
        tempDialog.setView(tempView)

        //2-2) View 참조
        val cInput = tempView.findViewById<EditText>(R.id.c_input)
        val fInput = tempView.findViewById<EditText>(R.id.f_input)

        //3-2) TextWatcher 설정
        //c 변경 감지
        cInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (cInput.isFocused){
                    val cValue = s.toString().toDoubleOrNull() ?:0.0
                    val fValue = cValue * (9.0/5.0) + 32

                    fInput.setText(String.format("%.2f", fValue))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //f 변경 감지
        fInput.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (fInput.isFocused){
                    val fValue = s.toString().toDoubleOrNull() ?:0.0
                    val cValue = (fValue - 32) / 1.8

                    cInput.setText(String.format("%.2f", cValue))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //4-2) 버튼 클릭 시 dialog.show()
        binding.conversionCardTemperature.setOnClickListener({
            tempDialog.show()
        })

        return root
    }

    /**
     * 환율 API를 호출하여 최신 데이터를 가져오는 함수
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchExchangeRate(date: LocalDate, count: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val authKey = "GkWSnQO1HtS8mCK47jMqVaSra3htcuXj"
            val searchDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val data = "AP01"
            val baseURL = "https://www.koreaexim.go.kr"
            var currentURL = "$baseURL/site/program/financial/exchangeJSON?authkey=$authKey&searchdate=$searchDate&data=$data"
            val visitedUrls = mutableSetOf<String>()
            var redirectCount = 0
            val maxRedirects = 10
            var successful = false

            try {
                while (redirectCount < maxRedirects && !successful) {
                    Log.d("OkHttpRedirect", "Requesting: $currentURL")
                    visitedUrls.add(currentURL)

                    val request = Request.Builder()
                        .url(currentURL)
                        .header("User-Agent", "Mozilla/5.0 (Linux; Android 11; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.93 Mobile Safari/537.36")
                        .header("Accept", "application/json")
                        .build()

                    val response = client.newCall(request).execute()
                    val code = response.code
                    Log.d("OkHttpRedirect", "Response code: $code")

                    when {
                        code in 300..399 -> {
                            val location = response.header("Location")
                            Log.w("OkHttpRedirect", "Redirect to: $location")

                            if (location.isNullOrEmpty()) {
                                Log.e("OkHttpRedirect", "No Location header. Stopping.")
                                break
                            }

                            currentURL = if (location.startsWith("http")) {
                                location
                            } else {
                                "$baseURL$location"
                            }
                            /*
                            if (visitedUrls.contains(currentURL)) {
                                Log.e("OkHttpRedirect", "Redirect loop detected. Stopping.")
                                break
                            }*/
                            redirectCount++
                        }
                        code == 200 -> {
                            val body = response.body?.string() ?: ""
                            Log.d("OkHttpRedirect", "Response Body: $body")

                            val extractedRate = extractDealBasR(body, "USD")?.replace(",", "") ?: "데이터 오류"
                            if(extractedRate=="데이터 오류" && count != 0){
                                Log.d("OkHttpRedirect", "error")
                                fetchExchangeRate(date.minusDays(1), count-1)
                            }
                            exchangeRate = extractDealBasR(body, "USD")?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                            Log.d("OkHttpRedirect", "Rate : $exchangeRate")
                            withContext(Dispatchers.Main) {
                                binding.exchangeRateDisplay.text = "1달러 당 $extractedRate 원"
                            }
                            successful = true
                        }
                        else -> {
                            Log.e("OkHttpRedirect", "Failed with code: $code")
                            break
                        }
                    }
                    response.close()
                }

                if (redirectCount >= maxRedirects) {
                    Log.e("OkHttpRedirect", "Too many redirects. Stopping.")
                }
            } catch (e: Exception) {
                Log.e("OkHttpRedirect", "Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * JSON 배열에서 특정 통화(currency)에 해당하는 "deal_bas_r" 값을 찾아 반환
     */
    private fun extractDealBasR(jsonString: String, currency: String): String? {
        val jsonArr = JSONArray(jsonString)
        for (i in 0 until jsonArr.length()) {
            val jsonObject = jsonArr.getJSONObject(i)
            Log.d("OkHttpRedirect", "this : $i, $jsonObject")
            Log.d("OkHttpRedirect", "this : ${jsonObject.getString("cur_unit")==currency}")
            if (jsonObject.getString("cur_unit") == currency) {
                Log.d("OkHttpRedirect", "correct : ${jsonObject.getString("deal_bas_r")}")
                return jsonObject.getString("deal_bas_r")
            }
        }
        return null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * EditText에 TextWatcher 추가 설정
     */
    private fun setupTextWatchers() {
        binding.wonInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.wonInput.isFocused) {
                    val wonValue = s.toString().toDoubleOrNull() ?: 0.0
                    val dollarValue = wonValue / exchangeRate
                    binding.dollarInput.setText(String.format("%.2f", dollarValue))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.dollarInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (binding.dollarInput.isFocused) {
                    val dollarValue = s.toString().toDoubleOrNull() ?: 0.0
                    val wonValue = dollarValue * exchangeRate
                    binding.wonInput.setText(String.format("%.0f", wonValue))
                }
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * SSL 인증서 검증을 비활성화한 OkHttpClient 생성
     */
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
                }
            )
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .cookieJar(SimpleCookieJar())
                .followRedirects(false)
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
