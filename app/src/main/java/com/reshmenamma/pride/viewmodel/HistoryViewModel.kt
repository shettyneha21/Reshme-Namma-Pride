package com.reshmenamma.pride.viewmodel

import androidx.lifecycle.ViewModel
import com.reshmenamma.pride.data.repository.ReshmeRepository

class HistoryViewModel(
    private val repository: ReshmeRepository
) : ViewModel() {
    val allHistoryItems = repository.getBatchHistory()
}