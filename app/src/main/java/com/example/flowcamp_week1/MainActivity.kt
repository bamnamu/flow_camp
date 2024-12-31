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

        // NavHostFragment 초기 상태 숨기기
        navHostFragment.visibility = View.GONE

        // 슬라이드와 BottomNavigationView 동기화
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                navHostFragment.visibility = View.GONE // ViewPager2 사용 중 NavHostFragment 숨기기
                navView.menu.getItem(position).isChecked = true
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
