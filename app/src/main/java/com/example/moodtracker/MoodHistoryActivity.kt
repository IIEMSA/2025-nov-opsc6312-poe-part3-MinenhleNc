package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Button

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var rvMoodHistory: RecyclerView
    private lateinit var adapter: MoodAdapter
    private val moodList = mutableListOf<UserMood>()

    private lateinit var moodDao: UserMoodDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_history)

        rvMoodHistory = findViewById(R.id.rvMoodHistory)
        adapter = MoodAdapter(moodList)
        rvMoodHistory.layoutManager = LinearLayoutManager(this)
        rvMoodHistory.adapter = adapter

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        setupBottomNavigation(bottomNav)

        // Initialize Room DAO
        moodDao = UserMoodDatabase.getDatabase(this).userMoodDao()
        val btnRewards = findViewById<Button>(R.id.btnRewards)
        btnRewards.setOnClickListener {
            startActivity(Intent(this, RewardsActivity::class.java))
        }

        fetchMoodHistory()
    }

    private fun fetchMoodHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val moods = moodDao.getAllMoods(userId) // no sorting needed

                withContext(Dispatchers.Main) {
                    moodList.clear()
                    moodList.addAll(moods)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MoodHistoryActivity, "Failed to load history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.menu.findItem(R.id.nav_history).isChecked = true
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_log -> {
                    startActivity(Intent(this, MoodLoggingActivity::class.java))
                    true
                }
                R.id.nav_history -> true
                R.id.nav_forecast -> {
                    startActivity(Intent(this, InsightsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
