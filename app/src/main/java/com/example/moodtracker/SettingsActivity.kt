package com.example.moodtracker

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File




class SettingsActivity : AppCompatActivity() {

    // UI elements
    private lateinit var btnEditProfile: CardView
    private lateinit var btnExportData: CardView
    private lateinit var btnDeleteAccount: CardView
    private lateinit var btnLanguage: CardView
    private lateinit var btnHelpSupport: CardView
    private lateinit var btnAppInfo: CardView
    private lateinit var btnLogout: CardView
    private lateinit var switchNotifications: Switch
    private lateinit var bottomNavigationView: BottomNavigationView

    // NEW: Map of translations for all menu items
    private val translationsMap = mapOf(
        "Edit Profile" to mapOf(
            "en" to "Edit Profile",
            "zu" to "Hlela Iphrofayela",
            "af" to "Redigeer Profiel"
        ),
        "Choose Language" to mapOf(
            "en" to "Choose Language",
            "zu" to "Khetha Ulimi",
            "af" to "Kies Taal"
        ),
        "Notifications" to mapOf(
            "en" to "Notifications",
            "zu" to "Izaziso",
            "af" to "Kennisgewings"
        ),
        "Export Data" to mapOf(
            "en" to "Export Data",
            "zu" to "Thumela Idatha",
            "af" to "Eksporteer Data"
        ),
        "Delete Account" to mapOf(
            "en" to "Delete Account",
            "zu" to "Susa I-akhawunti",
            "af" to "Verwyder Rekening"
        ),
        "App Info" to mapOf(
            "en" to "App Info",
            "zu" to "Ulwazi Lwe-App",
            "af" to "App-inligting"
        ),
        "Help & Support" to mapOf(
            "en" to "Help & Support",
            "zu" to "Usizo & Ukusekela",
            "af" to "Hulp & Ondersteuning"
        ),
        "Logout" to mapOf(
            "en" to "Logout",
            "zu" to "Phuma",
            "af" to "Teken Uit"
        )
    )

    // SharedPreferences key
    private val PREFS_NAME = "mood_prefs"
    private val LANGUAGE_KEY = "selected_language"

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val userId get() = auth.currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize UI elements
        btnEditProfile = findViewById(R.id.cardEditProfile)
        btnExportData = findViewById(R.id.cardExportData)
        btnDeleteAccount = findViewById(R.id.cardDeleteAccount)
        btnLanguage = findViewById(R.id.cardChooseLanguage)
        btnHelpSupport = findViewById(R.id.cardHelpSupport)
        btnAppInfo = findViewById(R.id.cardAppInfo)
        btnLogout = findViewById(R.id.cardLogout)
        switchNotifications = findViewById(R.id.switchNotifications)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // --- Multi-language setup ---
        val savedLang = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(LANGUAGE_KEY, "en") ?: "en"
        updateTextForLanguage(savedLang)

        btnLanguage.setOnClickListener { showLanguageDialog() }

        // --- Existing setup ---
        setupBottomNavigation()
        requestNotificationPermission()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
        loadNotificationPreference()

        btnEditProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
        btnHelpSupport.setOnClickListener { startActivity(Intent(this, SupportActivity::class.java)) }
        btnAppInfo.setOnClickListener { startActivity(Intent(this, AppInfoActivity::class.java)) }
        btnExportData.setOnClickListener { confirmExportData() }
        btnDeleteAccount.setOnClickListener { confirmDeleteAccount() }
        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (userId.isEmpty()) return@setOnCheckedChangeListener
            val map = mapOf("notificationsEnabled" to isChecked)
            firestore.collection("users").document(userId)
                .set(map, SetOptions.merge())
                .addOnSuccessListener {
                    val msg = if (isChecked) "Notifications enabled" else "Notifications disabled"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save notification setting", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // --- NEW: Function to update TextViews for the selected language ---
    private fun updateTextForLanguage(lang: String) {
        findViewById<TextView>(R.id.tvEditProfile).text = translationsMap["Edit Profile"]?.get(lang)
        findViewById<TextView>(R.id.tvChooseLanguage).text = translationsMap["Choose Language"]?.get(lang)
        findViewById<TextView>(R.id.tvNotifications).text = translationsMap["Notifications"]?.get(lang)
        findViewById<TextView>(R.id.tvExportData).text = translationsMap["Export Data"]?.get(lang)
        findViewById<TextView>(R.id.tvDeleteAccount).text = translationsMap["Delete Account"]?.get(lang)
        findViewById<TextView>(R.id.tvAppInfo).text = translationsMap["App Info"]?.get(lang)
        findViewById<TextView>(R.id.tvHelpSupport).text = translationsMap["Help & Support"]?.get(lang)
        findViewById<TextView>(R.id.tvLogout).text = translationsMap["Logout"]?.get(lang)
    }

    // --- NEW: Language selection dialog ---
    private fun showLanguageDialog() {
        val languages = arrayOf("English", "IsiZulu", "Afrikaans")
        val langCodes = arrayOf("en", "zu", "af")
        AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val selectedLang = langCodes[which]
                updateTextForLanguage(selectedLang)
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                    .putString(LANGUAGE_KEY, selectedLang)
                    .apply()
            }
            .show()
    }

    private fun loadNotificationPreference() {
        if (userId.isEmpty()) return
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val isEnabled = snapshot.getBoolean("notificationsEnabled") ?: true
                switchNotifications.isChecked = isEnabled
            }
            .addOnFailureListener {
                switchNotifications.isChecked = true // default on
            }
    }

    private fun confirmExportData() {
        AlertDialog.Builder(this)
            .setTitle("Export Data")
            .setMessage("Do you want to export your mood data to a file?")
            .setPositiveButton("Yes") { _, _ -> exportDataToFile() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("This will permanently delete your account and all data. Continue?")
            .setPositiveButton("Yes") { _, _ -> deleteAccountAndData() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun exportDataToFile() {
        if (userId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = UserMoodDatabase.getDatabase(this@SettingsActivity)
                val moodDao = db.userMoodDao()
                val moods = moodDao.getAllMoods(userId)

                if (moods.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SettingsActivity,
                            "No mood data to export",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val sb = StringBuilder()
                moods.forEach { mood ->
                    sb.append("Mood: ${mood.selectedMood}\n")
                    sb.append("Location: ${mood.location}\n")
                    sb.append("Weather: ${mood.weather}\n")
                    sb.append("Main Note: ${mood.mainNote}\n")
                    sb.append("Timestamp: ${mood.time}\n")
                    sb.append("--------------------\n")
                }

                val fileName = "MoodData_${System.currentTimeMillis()}.txt"
                val file = File(getExternalFilesDir(null), fileName)
                file.writeText(sb.toString())

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Data exported to: ${file.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()

                    // --- Notification after export ---
                    val notificationsEnabled = getSharedPreferences("mood_prefs", MODE_PRIVATE)
                        .getBoolean("notificationsEnabled", false)

                    if (notificationsEnabled) {
                        NotificationHelper.showNotification(
                            this@SettingsActivity,
                            "Mood Data Exported",
                            "Profile details updated! Keep your personal info sunny and safe.\uD83C\uDF1F "
                        )
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SettingsActivity,
                        "Export failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun deleteAccountAndData() {
        val user = auth.currentUser ?: return
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = UserMoodDatabase.getDatabase(this@SettingsActivity)
                val moodDao = db.userMoodDao()
                moodDao.getAllMoods(userId).forEach { /* delete logic */ }
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@SettingsActivity, "Account deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SettingsActivity, "Failed to delete account", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SettingsActivity, "Error deleting data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }


    private fun setupBottomNavigation() {
        bottomNavigationView.menu.findItem(R.id.nav_settings).isChecked = true
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, MainActivity::class.java)); true }
                R.id.nav_log -> { startActivity(Intent(this, MoodLoggingActivity::class.java)); true }
                R.id.nav_history -> { startActivity(Intent(this, MoodHistoryActivity::class.java)); true }
                R.id.nav_forecast -> { startActivity(Intent(this, InsightsActivity::class.java)); true }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
