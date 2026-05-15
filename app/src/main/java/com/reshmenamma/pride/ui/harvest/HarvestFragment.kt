package com.reshmenamma.pride.ui.harvest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.reshmenamma.pride.R
import com.reshmenamma.pride.databinding.FragmentHarvestBinding
import com.reshmenamma.pride.viewmodel.ClimateUiState
import com.reshmenamma.pride.viewmodel.ClimateViewModel
import com.reshmenamma.pride.viewmodel.ReshmeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HarvestFragment : Fragment() {

    private var _binding: FragmentHarvestBinding? = null
    private val binding get() = _binding!!

    private lateinit var climateViewModel: ClimateViewModel

    private var batchId = -1L
    private var batchIdInt = -1
    private var hasPrefilledHarvest = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHarvestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        batchId = arguments?.getLong("batchId", -1L) ?: -1L
        if (batchId == -1L) {
            Toast.makeText(requireContext(), "Error: Batch not found", Toast.LENGTH_SHORT).show()
            return
        }

        batchIdInt = batchId.toInt()

        val factory = ReshmeViewModelFactory(requireContext())
        climateViewModel = ViewModelProvider(this, factory)[ClimateViewModel::class.java]

        climateViewModel.loadHarvestForBatch(batchIdInt)

        climateViewModel.existingHarvest.observe(viewLifecycleOwner) { harvest ->
            if (harvest != null && !hasPrefilledHarvest) {
                hasPrefilledHarvest = true

                binding.etYieldKg.setText(harvest.cocoonYieldKg.toString())
                binding.etNotes.setText(harvest.notes)
                binding.tvHarvestSummary.visibility = View.VISIBLE
                binding.tvHarvestSummary.text =
                    "✅ Harvest recorded: ${harvest.cocoonYieldKg} kg of cocoons\n" +
                            "📅 Date: ${
                                SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                ).format(Date(harvest.harvestDate))
                            }"
                binding.btnSaveHarvest.text = "Update Harvest Record"
            }
        }

        binding.btnSaveHarvest.setOnClickListener {
            val yieldStr = binding.etYieldKg.text.toString().trim()
            val yieldKg = yieldStr.toFloatOrNull()

            if (yieldKg == null || yieldKg <= 0f) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid yield in kg (e.g. 12.5)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            climateViewModel.saveHarvest(
                batchId = batchIdInt,
                yieldKg = yieldKg,
                notes = binding.etNotes.text.toString().trim()
            )
        }

        climateViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ClimateUiState.HarvestSaved -> {
                    val yieldKg = binding.etYieldKg.text.toString().toFloatOrNull() ?: 0f

                    binding.etYieldKg.text?.clear()
                    binding.etNotes.text?.clear()

                    binding.tvHarvestSummary.visibility = View.VISIBLE
                    binding.tvHarvestSummary.text =
                        "✅ Harvest recorded: $yieldKg kg of cocoons\n" +
                                "📅 Date: ${
                                    SimpleDateFormat(
                                        "dd MMM yyyy",
                                        Locale.getDefault()
                                    ).format(Date())
                                }"

                    binding.btnSaveHarvest.text = "Update Harvest Record"

                    AlertDialog.Builder(requireContext())
                        .setTitle("🌿 Harvest Saved!")
                        .setMessage(
                            "Congratulations! Your batch harvest has been recorded.\n\n" +
                                    "🐛 Cocoon Yield: $yieldKg kg\n" +
                                    "📦 Batch marked as Completed\n" +
                                    "📋 Saved to Batch History\n\n" +
                                    "You can view this record anytime in the History tab."
                        )
                        .setPositiveButton("View History") { _, _ ->
                            findNavController().navigate(R.id.historyFragment)
                        }
                        .setNegativeButton("Go Home") { _, _ ->
                            findNavController().popBackStack()
                        }
                        .setCancelable(false)
                        .show()
                }

                is ClimateUiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}