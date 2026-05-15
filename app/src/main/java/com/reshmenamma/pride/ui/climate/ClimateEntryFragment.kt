package com.reshmenamma.pride.ui.climate

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.reshmenamma.pride.R
import com.reshmenamma.pride.databinding.FragmentClimateEntryBinding
import com.reshmenamma.pride.util.Constants
import com.reshmenamma.pride.viewmodel.BatchViewModel
import com.reshmenamma.pride.viewmodel.ClimateUiState
import com.reshmenamma.pride.viewmodel.ClimateViewModel
import com.reshmenamma.pride.viewmodel.ReshmeViewModelFactory
import java.util.Calendar

class ClimateEntryFragment : Fragment() {

    private var _binding: FragmentClimateEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var climateViewModel: ClimateViewModel
    private lateinit var batchViewModel: BatchViewModel
    private var currentInstar = 1
    private var batchIdInt = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClimateEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val batchIdLong = arguments?.getLong("batchId", -1L) ?: -1L
        if (batchIdLong == -1L) {
            Toast.makeText(requireContext(), "Error: Batch not found", Toast.LENGTH_SHORT).show()
            return
        }
        batchIdInt = batchIdLong.toInt()

        val factory = ReshmeViewModelFactory(requireContext())
        climateViewModel = ViewModelProvider(this, factory)[ClimateViewModel::class.java]
        batchViewModel = ViewModelProvider(this, factory)[BatchViewModel::class.java]

        batchViewModel.loadAndSyncBatch(batchIdLong)
        batchViewModel.selectedBatch.observe(viewLifecycleOwner) { batch ->
            batch?.let {
                currentInstar = it.currentInstar
                binding.tvInstarStage.text =
                    Constants.INSTAR_STAGES[currentInstar]?.name ?: "Instar $currentInstar"
                binding.tvInstarDesc.text =
                    Constants.INSTAR_STAGES[currentInstar]?.description ?: ""
                binding.tvBreedName.text = "Breed: ${it.breed}"
                val config = Constants.INSTAR_STAGES[currentInstar]
                if (config != null) {
                    binding.tvIdealRange.text =
                        "Ideal: ${config.idealTempMin}–${config.idealTempMax}°C | ${config.idealHumidityMin}–${config.idealHumidityMax}% RH"
                }
            }
        }

        binding.chipGroupTime.check(getSuggestedTimeChip())

        climateViewModel.loadTodayCount(batchIdInt)
        climateViewModel.todayLogCount.observe(viewLifecycleOwner) { count ->
            binding.tvLogsToday.text = "Entries today: $count / 3"
        }

        climateViewModel.getReadingHistoryForBatch(batchIdInt)
            .observe(viewLifecycleOwner) { readings ->
                binding.tvLogsToday.text = "Entries today: ${climateViewModel.todayLogCount.value ?: 0} / 3"
            }

        binding.btnSubmit.setOnClickListener {
            val timeOfDay = getSelectedTimeOfDay()
            climateViewModel.submitClimateReading(
                batchId = batchIdInt,
                tempStr = binding.etTemperature.text.toString(),
                humidityStr = binding.etHumidity.text.toString(),
                timeOfDay = timeOfDay,
                instar = currentInstar
            )
        }

        observeClimateState()
    }

    private fun observeClimateState() {
        climateViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ClimateUiState.Loading -> {
                    binding.btnSubmit.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                    binding.cardAdvice.visibility = View.VISIBLE
                    binding.tvAdvice.text = "🤖 Consulting AI advisor...\nPlease wait a moment..."
                    binding.cardAdvice.setCardBackgroundColor(Color.parseColor("#F3F8F5"))
                }

                is ClimateUiState.AdviceReady -> {
                    binding.btnSubmit.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    showAdvice(state)

                    binding.etTemperature.text?.clear()
                    binding.etHumidity.text?.clear()

                    climateViewModel.loadTodayCount(batchIdInt)
                    selectNextTimeChip()

                    climateViewModel.resetState()
                }

                is ClimateUiState.Error -> {
                    binding.btnSubmit.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    climateViewModel.resetState()
                }

                else -> {
                    binding.btnSubmit.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun showAdvice(state: ClimateUiState.AdviceReady) {
        binding.cardAdvice.visibility = View.VISIBLE
        try {
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            binding.cardAdvice.startAnimation(anim)
        } catch (e: Exception) { /* ignore */ }

        binding.tvAdvice.text = state.advice
        binding.tvReadingSummary.text =
            "Recorded: ${state.temperature}°C | ${state.humidity}% RH — ${Constants.INSTAR_STAGES[state.instar]?.name}"

        when (state.dialStatus) {
            Constants.STATUS_GREEN, "Optimal" -> {
                binding.climateDial.setBackgroundColor(Color.parseColor("#2E7D32"))
                binding.tvDialStatus.text = "✅ SAFE"
                binding.tvDialStatus.setTextColor(Color.WHITE)
                binding.cardAdvice.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
            }

            Constants.STATUS_ORANGE, "Moderate" -> {
                binding.climateDial.setBackgroundColor(Color.parseColor("#E65100"))
                binding.tvDialStatus.text = "⚠️ CAUTION"
                binding.tvDialStatus.setTextColor(Color.WHITE)
                binding.cardAdvice.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
            }

            else -> {
                binding.climateDial.setBackgroundColor(Color.parseColor("#B71C1C"))
                binding.tvDialStatus.text = "🚨 DANGER"
                binding.tvDialStatus.setTextColor(Color.WHITE)
                binding.cardAdvice.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
                try {
                    val pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse)
                    binding.climateDial.startAnimation(pulse)
                } catch (e: Exception) { /* ignore */ }
            }
        }
    }

    private fun getSelectedTimeOfDay(): String {
        return when (binding.chipGroupTime.checkedChipId) {
            R.id.chipMorning -> "MORNING"
            R.id.chipAfternoon -> "AFTERNOON"
            R.id.chipEvening -> "EVENING"
            else -> "MORNING"
        }
    }

    private fun getSuggestedTimeChip(): Int {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> R.id.chipMorning
            hour < 17 -> R.id.chipAfternoon
            else -> R.id.chipEvening
        }
    }

    private fun selectNextTimeChip() {
        when (binding.chipGroupTime.checkedChipId) {
            R.id.chipMorning -> binding.chipGroupTime.check(R.id.chipAfternoon)
            R.id.chipAfternoon -> binding.chipGroupTime.check(R.id.chipEvening)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}