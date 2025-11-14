package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SupportActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.menu.findItem(R.id.nav_settings).isChecked = true
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
                R.id.nav_log -> startActivity(Intent(this, MoodLoggingActivity::class.java))
                R.id.nav_history -> startActivity(Intent(this, MoodHistoryActivity::class.java))
                R.id.nav_forecast -> startActivity(Intent(this, InsightsActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            }
            true
        }
    }
}
