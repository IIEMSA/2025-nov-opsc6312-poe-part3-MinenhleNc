package com.example.moodtracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class InsightsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // --- 0️⃣ Load saved language before setting content view ---
        val prefs = getSharedPreferences("Settings", MODE_PRIVATE)
        val language = prefs.getString("App_Language", "en") // default to English
        language?.let { setLocale(it) }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)

        // --- 1️⃣ Setup Bottom Navigation ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        setupBottomNavigation(bottomNav)

        // --- 2️⃣ Setup Clickable Sunny Links ---
        setupSunnyLinks()
    }

    // --- Function to handle the sunny activity links ---
    private fun setupSunnyLinks() {
        val natureWalkIcon = findViewById<ImageView>(R.id.natureWalkIcon)
        val outdoorSportIcon = findViewById<ImageView>(R.id.outdoorSportIcon)
        val readBookIcon = findViewById<ImageView>(R.id.readBookIcon)

        val links = mapOf(
            natureWalkIcon to "https://www.healthline.com/health/benefits-of-walking-in-nature",
            outdoorSportIcon to "https://www.betterhealth.vic.gov.au/health/healthyliving/outdoor-sport-and-physical-activity",
            readBookIcon to "https://www.goodreads.com/genres/books-about-nature"
        )

        links.forEach { (icon, url) ->
            icon.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
    }

    // --- Bottom Navigation Setup ---
    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        // Set the current tab (Insights)
        bottomNav.menu.findItem(R.id.nav_forecast).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_log -> {
                    startActivity(Intent(this, MoodLoggingActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, MoodHistoryActivity::class.java))
                    true
                }
                R.id.nav_forecast -> true // current page
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // --- Locale helper function ---
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
