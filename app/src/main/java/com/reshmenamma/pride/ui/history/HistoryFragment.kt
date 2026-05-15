


package com.reshmenamma.pride.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.reshmenamma.pride.databinding.FragmentHistoryBinding
import com.reshmenamma.pride.ui.adapter.HistoryAdapter
import com.reshmenamma.pride.viewmodel.HistoryViewModel
import com.reshmenamma.pride.viewmodel.ReshmeViewModelFactory

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ReshmeViewModelFactory(requireContext())
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        adapter = HistoryAdapter()
        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistory.adapter = adapter

        historyViewModel.allHistoryItems.observe(viewLifecycleOwner) { historyItems ->
            if (historyItems.isEmpty()) {
                binding.tvEmptyHistory.visibility = View.VISIBLE
                binding.recyclerHistory.visibility = View.GONE
            } else {
                binding.tvEmptyHistory.visibility = View.GONE
                binding.recyclerHistory.visibility = View.VISIBLE
                adapter.submitList(historyItems)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}