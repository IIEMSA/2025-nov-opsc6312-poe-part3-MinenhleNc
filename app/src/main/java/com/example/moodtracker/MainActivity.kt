package com.example.moodtracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.*
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



// ---------- MAIN ACTIVITY ----------
class MainActivity : AppCompatActivity() {

    companion object { const val LOCATION_REQUEST = 100 }

    private lateinit var tvGreeting: TextView
    private lateinit var tvMoodStatus: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvWeather: TextView
    private lateinit var etCity: EditText
    private lateinit var etMoodNotes: EditText
    private lateinit var btnUseCity: Button
    private lateinit var btnSaveNote: Button
    private lateinit var btnLogMood: Button

    private lateinit var userMoodDao: UserMoodNoteDao

    private var currentLocation = ""
    private var currentWeather = ""
    private var currentTemp = 0.0

    private val apiKey by lazy { getString(R.string.weather_api_key) }

    // Firebase
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomNavigation()

        // Initialize views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvMoodStatus = findViewById(R.id.tvMoodStatus)
        tvLocation = findViewById(R.id.tvLocation)
        tvWeather = findViewById(R.id.tvWeather)
        etCity = findViewById(R.id.etCity)
        etMoodNotes = findViewById(R.id.etMoodNotes)
        btnUseCity = findViewById(R.id.btnUseCity)
        btnSaveNote = findViewById(R.id.btnSaveNote)
        btnLogMood = findViewById(R.id.btnLogMood)

        // Initialize DAO
        userMoodDao = UserMoodNoteDatabase.getDatabase(this).userMoodNoteDao()

        // Greeting
        val currentUserId = auth.currentUser?.uid ?: "unknown"
        tvGreeting.text = "Hello Sunshine 🌤️"

        // Request location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) getLocation() else ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST
        )

        // Manual city input
        btnUseCity.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(this, "Enter a city!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentLocation = city
            tvLocation.text = city
            fetchWeather(city = city)
        }

        // Save mood note
        // Save mood note
        btnSaveNote.setOnClickListener {
            val noteText = etMoodNotes.text.toString().trim()
            if (noteText.isEmpty()) {
                Toast.makeText(this, "Enter a note first!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val timestamp = System.currentTimeMillis()
            tvMoodStatus.text = noteText

            lifecycleScope.launch(Dispatchers.IO) {
                val note = UserMoodNote(
                    userId = currentUserId,
                    timestamp = timestamp,
                    location = currentLocation,
                    weather = currentWeather,
                    temperature = currentTemp,
                    note = noteText,
                    isSynced = false
                )
                userMoodDao.insert(note)

                // Push to Firebase
                db.child("user_mood_notes")
                    .child(currentUserId)
                    .push()
                    .setValue(note)
                    .addOnSuccessListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            userMoodDao.insert(note.copy(isSynced = true))
                        }
                    }
            }

            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
            etMoodNotes.text.clear()

            // --- Trigger notification ---
            val notificationsEnabled = getSharedPreferences("mood_prefs", MODE_PRIVATE)
                .getBoolean("notificationsEnabled", false)

            if (notificationsEnabled) {
                NotificationHelper.showNotification(
                    this,
                    "Mood Note Saved",
                    "Your thoughts are safely stored in the mood clouds!\uD83C\uDF26\uFE0F"
                )
            }
        }


        // Navigate to Mood Logging
        btnLogMood.setOnClickListener {
            startActivity(Intent(this, MoodLoggingActivity::class.java))
        }

        // Load latest note on startup
        lifecycleScope.launch(Dispatchers.IO) {
            val latestNote = userMoodDao.getLatestNote(currentUserId)
            latestNote?.let {
                withContext(Dispatchers.Main) {
                    tvMoodStatus.text = it.note
                    tvLocation.text = it.location
                    tvWeather.text = "${it.weather}, ${it.temperature}°C"
                    currentLocation = it.location
                    currentWeather = it.weather
                    currentTemp = it.temperature
                }
            }
        }
    }

    // ----------------- LOCATION -----------------
    private fun getLocation() {
        val client = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        client.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) fetchWeather(lat = loc.latitude, lon = loc.longitude)
            else tvLocation.text = "Location unavailable"
        }
    }


    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Highlight current tab
        bottomNav.menu.findItem(R.id.nav_home).isChecked = true

        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> true // Already on Home, do nothing
                R.id.nav_log -> {
                    startActivity(Intent(this, MoodLoggingActivity::class.java))
                    true
                }
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

    // ----------------- WEATHER -----------------
    private fun fetchWeather(city: String? = null, lat: Double? = null, lon: Double? = null) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherAPI::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = when {
                    city != null -> service.getByCity(city, apiKey, "metric")
                    lat != null && lon != null -> service.getByCoords(lat, lon, apiKey, "metric")
                    else -> null
                }
                response?.let {
                    withContext(Dispatchers.Main) {
                        currentLocation = it.name
                        tvLocation.text = currentLocation
                        currentWeather = it.weather[0].main
                        currentTemp = it.main.temp
                        tvWeather.text = "${currentWeather}, ${currentTemp}°C"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Weather fetch failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        }
    }
}

// Retrofit API for Weather
interface WeatherAPI {
    @GET("weather")
    suspend fun getByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): WeatherResponse

    @GET("weather")
    suspend fun getByCoords(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): WeatherResponse
}

data class WeatherResponse(val name: String, val main: Main, val weather: List<WeatherItem>)
data class Main(val temp: Double)
data class WeatherItem(val main: String)
/ /   A u t h o r :   A b o n g i l e   M a z o n g o l o 
 / /   A d d   n u l l   c h e c k s   i n   M a i n A c t i v i t y  
 