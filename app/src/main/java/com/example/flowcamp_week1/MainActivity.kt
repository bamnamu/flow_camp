package com.example.flowcamp_week1

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.flowcamp_week1.databinding.ActivityMainBinding
import com.example.flowcamp_week1.utils.ViewPagerAdapter
import com.example.flowcamp_week1.ui.dashboard.*
import com.example.flowcamp_week1.ui.home.*
import com.example.flowcamp_week1.ui.notifications.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기존 BottomNavigationView와 NavController 설정 유지
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navHostFragment = findViewById<View>(R.id.nav_host_fragment_activity_main) // NavHostFragment 가져오기
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // ViewPager2 설정 추가
        val viewPager: ViewPager2 = binding.viewPager // ViewPager2를 XML에서 가져옴
        val fragments = listOf(
            HomeFragment(),
            DashboardFragment(), // 이미 구현된 DashboardFragment
            NotificationsFragment() // 또 다른 탭 프래그먼트
        )
        val adapter = ViewPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        // 초기 상태 설정
        viewPager.currentItem = 0
        navView.menu.getItem(0).isChecked = true
        supportActionBar?.title = "Helplines" // 초기 제목 설정
        navHostFragment.visibility = View.GONE

        // ViewPager2 슬라이드와 BottomNavigationView 동기화
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                navHostFragment.visibility = View.GONE // ViewPager2 사용 중 NavHostFragment 숨기기
                navView.menu.getItem(position).isChecked = true
                // 페이지 전환 시 상단 제목 업데이트
                supportActionBar?.title = when (position) {
                    0 -> "Helplines"
                    1 -> "Attractions"
                    2 -> "Utilities"
                    else -> "Utilities"
                }
            }
        })

        // BottomNavigationView 클릭 시 ViewPager 전환
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewPager.currentItem = 0
                    navHostFragment.visibility = View.GONE
                    true
                }
                R.id.navigation_dashboard -> {
                    viewPager.currentItem = 1
                    navHostFragment.visibility = View.GONE
                    true
                }
                R.id.navigation_notifications -> {
                    viewPager.currentItem = 2
                    navHostFragment.visibility = View.GONE
                    true
                }
                else -> {
                    navHostFragment.visibility = View.VISIBLE // NavHostFragment 다시 보이기
                    false
                }
            }
        }
    }
}
