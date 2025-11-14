package com.example.moodtracker

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BadgeAdapter(
    private var badges: List<Badge>
) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(badges[position])
    }

    override fun getItemCount() = badges.size

    fun updateList(newBadges: List<Badge>) {
        badges = newBadges
        notifyDataSetChanged()
    }

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val badgeIcon: ImageView = itemView.findViewById(R.id.badgeIcon)
        private val badgeTitle: TextView = itemView.findViewById(R.id.badgeTitle)
        private val cardView: CardView = itemView as CardView

        fun bind(badge: Badge) {
            val context = itemView.context
            badgeTitle.text = badge.title
            badgeIcon.setImageResource(badge.iconRes)

            val badgeColors = listOf(
                R.color.red_badge,
                R.color.orange_badge,
                R.color.yellow_badge,
                R.color.green_badge,
                R.color.teal_badge,
                R.color.blue_badge,
                R.color.purple_badge,
                R.color.pink_badge
            )

            val colorRes = if (badge.unlocked)
                badgeColors.getOrNull(badge.id - 1) ?: R.color.gray_badge
            else
                R.color.gray_badge

            val color = ContextCompat.getColor(context, colorRes)
            cardView.setCardBackgroundColor(color)
            badgeIcon.alpha = if (badge.unlocked) 1f else 0.3f

            itemView.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(badge.title)
                    .setMessage(badge.description)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
