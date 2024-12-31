package com.example.flowcamp_week1.ui.notifications

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    // 임시 환율값. API 결과로 업데이트됨.
    private var exchangeRate = 1000.0

    // OkHttpClient 인스턴스 생성 (SSL 검증 비활성화 포함)
    private val client: OkHttpClient by lazy { getUnsafeOkHttpClient() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root = binding.root

        // 환율 호출
        fetchExchangeRate()

        // EditText TextWatcher 설정
        setupTextWatchers()

        // 초기 환율 표시
        binding.exchangeRateDisplay.text = "1달러 당 $exchangeRate 원"
        return root
    }

    /**
     * 환율 API를 호출하여 최신 데이터를 가져오는 함수
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchExchangeRate() {
        CoroutineScope(Dispatchers.IO).launch {
            val authKey = "GkWSnQO1HtS8mCK47jMqVaSra3htcuXj"
            val searchDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
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
