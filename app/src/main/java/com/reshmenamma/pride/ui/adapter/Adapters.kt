package com.reshmenamma.pride.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.reshmenamma.pride.data.model.Batch
import com.reshmenamma.pride.databinding.ItemBatchBinding
import com.reshmenamma.pride.databinding.ItemHistoryBinding
import com.reshmenamma.pride.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.reshmenamma.pride.data.model.BatchHistoryItem

// ─── Batch Adapter ────────────────────────────────────────────────────────────

class BatchAdapter(
    private val onBatchClick: (Batch) -> Unit,
    private val onHarvestClick: (Batch) -> Unit,
    private val onDeleteClick: (Batch) -> Unit,
    private val isHarvestReady: (Batch) -> Boolean
) : ListAdapter<Batch, BatchAdapter.BatchViewHolder>(BatchDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class BatchViewHolder(private val binding: ItemBatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(batch: Batch) {
            val calculatedInstar = calculateCurrentInstar(batch.startDate)
            val config = Constants.INSTAR_STAGES[calculatedInstar]

            binding.tvBreed.text = batch.breed
            binding.tvStartDate.text = "Started: ${dateFormat.format(Date(batch.startDate))}"
            binding.tvInstar.text = config?.name ?: "Instar $calculatedInstar"
            binding.tvInstarDesc.text = config?.description ?: ""

            val harvestReady = isHarvestReady(batch)
            if (harvestReady) {
                binding.tvHarvestStatus.text = "🌿 Ready for Harvest!"
                binding.tvHarvestStatus.setTextColor(Color.parseColor("#1B5E20"))
                binding.btnHarvest.visibility = View.VISIBLE
            } else {
                val daysLeft = Constants.TOTAL_REARING_DAYS -
                        ((System.currentTimeMillis() - batch.startDate) / (1000L * 60 * 60 * 24)).toInt()
                binding.tvHarvestStatus.text = "⏳ ~$daysLeft days to harvest"
                binding.tvHarvestStatus.setTextColor(Color.parseColor("#5D4037"))
                binding.btnHarvest.visibility = View.GONE
            }

            binding.root.setOnClickListener { onBatchClick(batch) }
            binding.btnEnterClimate.setOnClickListener { onBatchClick(batch) }
            binding.btnHarvest.setOnClickListener { onHarvestClick(batch) }

            binding.root.setOnLongClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setTitle("🗑️ Delete Batch")
                    .setMessage("Delete '${batch.breed}' batch?\n\nThis will also delete all climate logs for this batch. This cannot be undone.")
                    .setPositiveButton("Delete") { _, _ -> onDeleteClick(batch) }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }

        private fun calculateCurrentInstar(startDateMillis: Long): Int {
            val daysSinceStart =
                ((System.currentTimeMillis() - startDateMillis) / (1000L * 60 * 60 * 24)).toInt()

            if (daysSinceStart >= Constants.TOTAL_REARING_DAYS) return 5

            var cumulativeDays = 0
            for (stage in 1..5) {
                cumulativeDays += Constants.INSTAR_STAGES[stage]?.durationDays ?: 0
                if (daysSinceStart < cumulativeDays) return stage
            }
            return 5
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val binding = ItemBatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BatchDiffCallback : DiffUtil.ItemCallback<Batch>() {
    override fun areItemsTheSame(oldItem: Batch, newItem: Batch) = oldItem.batchId == newItem.batchId
    override fun areContentsTheSame(oldItem: Batch, newItem: Batch) = oldItem == newItem
}

// ─── History Adapter ──────────────────────────────────────────────────────────

class HistoryAdapter :
    ListAdapter<BatchHistoryItem, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BatchHistoryItem) {
            binding.tvBreed.text = item.breed
            binding.tvStartDate.text = "Started: ${dateFormat.format(Date(item.startDate))}"

            if (item.status == "COMPLETED") {
                binding.tvStatus.text = "✅ Completed"
                binding.tvStatus.setTextColor(Color.WHITE)
                binding.tvStatus.setBackgroundColor(Color.parseColor("#00008B"))

                if (item.cocoonYieldKg != null) {
                    binding.tvCocoonYield.visibility = View.VISIBLE
                    binding.tvCocoonYield.text = "Cocoon yield: ${item.cocoonYieldKg} kg"
                } else {
                    binding.tvCocoonYield.visibility = View.GONE
                    binding.tvCocoonYield.text = ""
                }

                if (item.harvestDate != null) {
                    binding.tvHarvestDate.visibility = View.VISIBLE
                    binding.tvHarvestDate.text = "Harvested: ${dateFormat.format(Date(item.harvestDate))}"
                } else {
                    binding.tvHarvestDate.visibility = View.GONE
                    binding.tvHarvestDate.text = ""
                }

                binding.tvNotes.visibility = View.VISIBLE
                binding.tvNotes.text = "Note: ${item.notes ?: "[empty]"}"

            } else {
                binding.tvStatus.text = "🌱 Active"
                binding.tvStatus.setTextColor(Color.WHITE)
                binding.tvStatus.setBackgroundColor(Color.parseColor("#121212"))

                binding.tvCocoonYield.visibility = View.GONE
                binding.tvCocoonYield.text = ""

                binding.tvHarvestDate.visibility = View.GONE
                binding.tvHarvestDate.text = ""

                binding.tvNotes.visibility = View.GONE
                binding.tvNotes.text = ""
            }

            val px = (8 * binding.root.context.resources.displayMetrics.density).toInt()
            binding.tvStatus.setPadding(px, px / 2, px, px / 2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class HistoryDiffCallback : DiffUtil.ItemCallback<BatchHistoryItem>() {
    override fun areItemsTheSame(oldItem: BatchHistoryItem, newItem: BatchHistoryItem) =
        oldItem.batchId == newItem.batchId

    override fun areContentsTheSame(oldItem: BatchHistoryItem, newItem: BatchHistoryItem) =
        oldItem == newItem
}