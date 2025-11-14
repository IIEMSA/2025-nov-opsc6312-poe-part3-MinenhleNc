package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RewardsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var adapter: BadgeAdapter
    private lateinit var moodDao: UserMoodDao

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        recyclerView = findViewById(R.id.recyclerBadges)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val backButton = findViewById<ImageView>(R.id.backButton)

        moodDao = UserMoodDatabase.getDatabase(this).userMoodDao()

        adapter = BadgeAdapter(badges)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        backButton.setOnClickListener { finish() }
        setupBottomNavigation()

        updateBadges()
    }

    private fun updateBadges() {
        if (userId.isEmpty()) return

        lifecycleScope.launch(Dispatchers.IO) {
            val moodCount = try {
                moodDao.countMoodsForUser(userId)
            } catch (e: Exception) {
                0
            }

            val updatedBadges = badges.map { badge ->
                badge.copy(unlocked = moodCount >= badge.unlockThreshold)
            }

            withContext(Dispatchers.Main) {
                adapter.updateList(updatedBadges)
            }
        }
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
