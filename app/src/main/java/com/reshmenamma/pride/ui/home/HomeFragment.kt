package com.reshmenamma.pride.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reshmenamma.pride.R
import com.reshmenamma.pride.databinding.FragmentHomeBinding
import com.reshmenamma.pride.ui.adapter.BatchAdapter
import com.reshmenamma.pride.viewmodel.BatchViewModel
import com.reshmenamma.pride.viewmodel.ReshmeViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var batchViewModel: BatchViewModel
    private lateinit var adapter: BatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ReshmeViewModelFactory(requireContext())
        batchViewModel = ViewModelProvider(this, factory)[BatchViewModel::class.java]

        setupRecyclerView()
        observeBatches()

        binding.fabNewBatch.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_newBatch)
        }
    }

    private fun setupRecyclerView() {
        adapter = BatchAdapter(
            onBatchClick = { batch ->
                val bundle = Bundle().apply { putLong("batchId", batch.batchId.toLong()) }  // ← .toLong()
                findNavController().navigate(R.id.action_home_to_batchDetail, bundle)
            },
            onHarvestClick = { batch ->
                val bundle = Bundle().apply { putLong("batchId", batch.batchId.toLong()) }  // ← .toLong()
                findNavController().navigate(R.id.action_home_to_harvest, bundle)
            },
            onDeleteClick = { batch ->
                batchViewModel.deleteBatch(batch)
            },
            isHarvestReady = { batch -> batchViewModel.isHarvestReady(batch) }
        )
        binding.recyclerBatches.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBatches.adapter = adapter
    }

    private fun observeBatches() {
        batchViewModel.activeBatches.observe(viewLifecycleOwner) { batches ->
            if (batches.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.recyclerBatches.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.recyclerBatches.visibility = View.VISIBLE
                adapter.submitList(batches)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}