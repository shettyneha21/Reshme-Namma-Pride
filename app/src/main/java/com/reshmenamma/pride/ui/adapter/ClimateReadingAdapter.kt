package com.reshmenamma.pride.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reshmenamma.pride.data.entity.ClimateReadingEntity
import com.reshmenamma.pride.databinding.ItemClimateReadingBinding

class ClimateReadingAdapter :
    ListAdapter<ClimateReadingEntity, ClimateReadingAdapter.ClimateReadingViewHolder>(ClimateReadingDiffCallback()) {

    inner class ClimateReadingViewHolder(
        private val binding: ItemClimateReadingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClimateReadingEntity) {
            binding.tvReadingDate.text = "Date: ${item.readingDate}"
            binding.tvTimeSlot.text = "Time: ${item.timeSlot}"
            binding.tvTemperature.text = "Temperature: ${item.temperature}°C"
            binding.tvHumidity.text = "Humidity: ${item.humidity}%"

            if (item.feedGiven.isNotBlank()) {
                binding.tvFeedGiven.text = "Feed: ${item.feedGiven}"
            } else {
                binding.tvFeedGiven.text = "Feed: -"
            }

            if (item.notes.isNotBlank()) {
                binding.tvNotes.text = "Notes: ${item.notes}"
            } else {
                binding.tvNotes.text = "Notes: -"
            }

            val temp = item.temperature
            binding.viewStatusColor.setBackgroundColor(
                when {
                    temp < 24f || temp > 30f -> Color.parseColor("#D32F2F")
                    temp in 26f..28f -> Color.parseColor("#2E7D32")
                    else -> Color.parseColor("#F9A825")
                }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClimateReadingViewHolder {
        val binding = ItemClimateReadingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClimateReadingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClimateReadingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ClimateReadingDiffCallback : DiffUtil.ItemCallback<ClimateReadingEntity>() {
    override fun areItemsTheSame(
        oldItem: ClimateReadingEntity,
        newItem: ClimateReadingEntity
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ClimateReadingEntity,
        newItem: ClimateReadingEntity
    ): Boolean {
        return oldItem == newItem
    }
}