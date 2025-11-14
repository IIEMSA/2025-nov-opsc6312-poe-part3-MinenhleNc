package com.example.moodtracker

data class Badge(
    val id: Int,
    val title: String,
    val description: String,
    val unlockThreshold: Int,
    val iconRes: Int,            // new icon resource for each badge
    var unlocked: Boolean = false
)

// Define badges with unique icons
val badges = listOf(
    Badge(1, "First Log", "Logged your first mood!", 1, R.drawable.ic_badge_first),
    Badge(2, "Lightning Tracker", "2 moods logged", 4, R.drawable.ic_badge_lightning),
    Badge(3, "Raindrop Rider", "3 moods logged", 8, R.drawable.ic_badge_rain),
    Badge(4, "Snowflake Friend", "4 moods logged", 12, R.drawable.ic_badge_snow),
    Badge(5, "Hail Hero", "5 moods logged", 16, R.drawable.ic_badge_hail),
    Badge(6, "Cloud Collector", "6 moods logged", 20, R.drawable.ic_badge_cloud),
    Badge(7, "Wind Whisperer", "7 moods logged", 24, R.drawable.ic_badge_wind),
    Badge(8, "Sun Seeker", "8 moods logged", 28, R.drawable.ic_badge_sun)
)

