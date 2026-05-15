package com.reshmenamma.pride.ui.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.reshmenamma.pride.databinding.FragmentNewBatchBinding
import com.reshmenamma.pride.viewmodel.BatchUiState
import com.reshmenamma.pride.viewmodel.BatchViewModel
import com.reshmenamma.pride.viewmodel.ReshmeViewModelFactory
import com.reshmenamma.pride.worker.HarvestScheduler
import java.text.SimpleDateFormat
import java.util.*

class NewBatchFragment : Fragment() {

    private var _binding: FragmentNewBatchBinding? = null
    private val binding get() = _binding!!
    private lateinit var batchViewModel: BatchViewModel
    private var selectedDateMillis = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewBatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ReshmeViewModelFactory(requireContext())
        batchViewModel = ViewModelProvider(this, factory)[BatchViewModel::class.java]

        // Set today's date as default
        binding.tvStartDate.text = dateFormat.format(Date(selectedDateMillis))

        binding.btnPickDate.setOnClickListener { showDatePicker() }

        binding.btnCreateBatch.setOnClickListener {
            val breed = binding.etBreed.text.toString()
            batchViewModel.createBatch(breed, selectedDateMillis)
        }

        observeState()
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                cal.set(year, month, day, 0, 0, 0)
                selectedDateMillis = cal.timeInMillis
                binding.tvStartDate.text = dateFormat.format(cal.time)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observeState() {
        batchViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BatchUiState.Loading -> {
                    binding.btnCreateBatch.isEnabled = false
                    binding.progressBar.visibility = View.VISIBLE
                }
                is BatchUiState.Success -> {
                    // Schedule harvest alert
                    val breed = binding.etBreed.text.toString()
                    HarvestScheduler.scheduleHarvestAlert(
                        requireContext(), state.batchId, breed, selectedDateMillis
                    )
                    Toast.makeText(requireContext(), "✅ Batch created! Good luck!", Toast.LENGTH_SHORT).show()
                    batchViewModel.resetState()
                    findNavController().popBackStack()
                }
                is BatchUiState.Error -> {
                    binding.btnCreateBatch.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    batchViewModel.resetState()
                }
                else -> {
                    binding.btnCreateBatch.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
