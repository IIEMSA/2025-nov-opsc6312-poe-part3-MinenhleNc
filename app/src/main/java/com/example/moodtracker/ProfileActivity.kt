package com.example.moodtracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAltPhone: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnBack: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAltPhone = findViewById(R.id.etAltPhone)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnBack = findViewById(R.id.btnBack)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Pre-fill user data
        loadUserInfo()

        // Button listeners
        btnBack.setOnClickListener { finish() } // Go back
        btnUpdateProfile.setOnClickListener { updateProfile() }

        setupBottomNavigation()
    }

    private fun loadUserInfo() {
        val user = auth.currentUser ?: return

        // Split display name into name and surname
        val displayName = user.displayName ?: ""
        val nameParts = displayName.split(" ", limit = 2)
        etName.setText(nameParts.getOrElse(0) { "" })
        etSurname.setText(nameParts.getOrElse(1) { "" })

        // Email (non-editable)
        etEmail.setText(user.email ?: "")

        // Load phone numbers from Firestore
        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { snapshot ->
                etPhone.setText(snapshot.getString("phone") ?: "")
                etAltPhone.setText(snapshot.getString("altPhone") ?: "")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateProfile() {
        val user = auth.currentUser ?: return
        val name = etName.text.toString().trim()
        val surname = etSurname.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val altPhone = etAltPhone.text.toString().trim()

        if (name.isEmpty() || surname.isEmpty()) {
            Toast.makeText(this, "Please enter name and surname", Toast.LENGTH_SHORT).show()
            return
        }

        // Update Firebase Authentication display name
        val fullName = "$name $surname"
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(fullName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save phone numbers to Firestore
                    val userMap = mapOf(
                        "phone" to phone,
                        "altPhone" to altPhone
                    )
                    firestore.collection("users").document(user.uid)
                        .set(userMap, SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()

                            // --- Notification for profile update ---
                            val notificationsEnabled = getSharedPreferences("mood_prefs", MODE_PRIVATE)
                                .getBoolean("notificationsEnabled", false)

                            if (notificationsEnabled) {
                                NotificationHelper.showNotification(
                                    this,
                                    "Profile Information Updated",
                                    "🌞 Profile details updated! Keep your personal info sunny and safe."
                                )
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save phone numbers", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
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
