package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MoodLoggingActivity : AppCompatActivity() {

    private lateinit var moodGrid: GridLayout
    private lateinit var btnSaveMood: Button
    private lateinit var tvLocation: TextView
    private lateinit var tvWeather: TextView
    private lateinit var tvNotes: TextView
    private lateinit var tvMoodTime: TextView

    private var selectedMood: String? = null
    private var mainLocation = "Unknown"
    private var mainWeather = "Unknown"
    private var mainNote = ""

    private val moods = listOf(
        "Heartbroken", "Sick", "Tired", "Anxious", "Angry",
        "Very Sad", "Sad", "Exhausted",
        "In Love", "Neutral", "Loved", "Cool", "Good", "Happy", "Ecstatic"
    )

    private lateinit var moodDao: UserMoodDao
    private lateinit var userNoteDao: UserMoodNoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_logging)

        // Initialize views
        moodGrid = findViewById(R.id.moodGridd)
        btnSaveMood = findViewById(R.id.btnSaveMood)
        tvLocation = findViewById(R.id.tvLocation)
        tvWeather = findViewById(R.id.tvWeather)
        tvNotes = findViewById(R.id.tvNotes)
        tvMoodTime = findViewById(R.id.tvMoodTime)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        setupBottomNavigation(bottomNav)

        // Initialize DAOs
        moodDao = UserMoodDatabase.getDatabase(this).userMoodDao()
        userNoteDao = UserMoodNoteDatabase.getDatabase(this).userMoodNoteDao()

        // Fetch latest user note
        fetchLatestNote()

        // Create mood buttons
        createMoodButtons()

        // Save button listener
        btnSaveMood.setOnClickListener { saveMood() }
    }

    private fun fetchLatestNote() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            val latestNote = userNoteDao.getLatestNote(userId)
            latestNote?.let {
                withContext(Dispatchers.Main) {
                    mainLocation = it.location
                    mainWeather = it.weather
                    mainNote = it.note

                    tvLocation.text = "Location: $mainLocation"
                    tvWeather.text = "Weather: $mainWeather"
                    tvNotes.text = "Notes: $mainNote"
                    tvMoodTime.text = "Time: ${getCurrentTime()}"
                }
            }
        }
    }

    private fun createMoodButtons() {
        moodGrid.removeAllViews()
        for (mood in moods) {
            val button = Button(this).apply {
                text = mood
                setOnClickListener {
                    selectedMood = mood
                    highlightSelected(this)
                }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            moodGrid.addView(button, params)
        }
    }

    private fun highlightSelected(selectedButton: Button) {
        for (i in 0 until moodGrid.childCount) {
            val btn = moodGrid.getChildAt(i) as Button
            btn.alpha = 1.0f
        }
        selectedButton.alpha = 0.6f
    }

    private fun saveMood() {

        if (selectedMood == null) {
            Toast.makeText(this, "Select a mood first", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val timeOnly = getCurrentTime()


        val moodEntry = UserMood(
            userId = userId,
            location = mainLocation,
            weather = mainWeather,
            mainNote = mainNote,
            selectedMood = selectedMood!!,
            time = getCurrentTime(),

        )

        lifecycleScope.launch(Dispatchers.IO) {
            // Save to Room
            moodDao.insert(moodEntry)

            // Save to Firebase
            val ref = FirebaseDatabase.getInstance().getReference("user_moods").child(userId)
            val key = ref.push().key
            key?.let { ref.child(it).setValue(moodEntry) }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@MoodLoggingActivity, "Mood logged successfully!", Toast.LENGTH_SHORT).show()
                selectedMood = null
                tvMoodTime.text = "Time: ${getCurrentTime()}"
                createMoodButtons() // reset buttons
            }
        }

        val notificationsEnabled = getSharedPreferences("mood_prefs", MODE_PRIVATE)
            .getBoolean("notificationsEnabled", false)

        if (notificationsEnabled) {
            NotificationHelper.showNotification(
                this,
                "Mood Logged Successfully",
                "🌤️ Yay! You’ve captured your mood today!"
            )
        }

        Toast.makeText(this, "Mood saved!", Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentTime(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.menu.findItem(R.id.nav_log).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_log -> true
                R.id.nav_history -> {
                    startActivity(Intent(this, MoodHistoryActivity::class.java))
                    true
                }
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
