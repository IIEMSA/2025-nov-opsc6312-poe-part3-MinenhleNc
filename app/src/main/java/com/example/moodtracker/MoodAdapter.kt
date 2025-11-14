package com.example.moodtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MoodAdapter(private val moods: List<UserMood>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moods[position]
        holder.tvMood.text = "Mood logged: ${mood.selectedMood}"
        holder.tvWeather.text = "Weather: ${mood.weather}"
        holder.tvNote.text = "Note made: ${mood.mainNote}"

    }

    override fun getItemCount(): Int = moods.size

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMood: TextView = itemView.findViewById(R.id.tvMood)
        val tvWeather: TextView = itemView.findViewById(R.id.tvWeather)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val card: CardView = itemView.findViewById(R.id.cardMood)
    }
}
