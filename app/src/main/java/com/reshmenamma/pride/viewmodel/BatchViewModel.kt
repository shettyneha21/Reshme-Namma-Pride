package com.reshmenamma.pride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reshmenamma.pride.data.model.Batch
import com.reshmenamma.pride.data.repository.ReshmeRepository
import kotlinx.coroutines.launch

sealed class BatchUiState {
    object Idle : BatchUiState()
    object Loading : BatchUiState()
    data class Success(val batchId: Long) : BatchUiState()
    data class Error(val message: String) : BatchUiState()
}

class BatchViewModel(
    private val repository: ReshmeRepository
) : ViewModel() {

    val activeBatches: LiveData<List<Batch>> = repository.getAllBatches()
    val allBatches: LiveData<List<Batch>> = repository.getAllBatches()

    private val _uiState = MutableLiveData<BatchUiState>(BatchUiState.Idle)
    val uiState: LiveData<BatchUiState> = _uiState

    private val _selectedBatch = MutableLiveData<Batch?>()
    val selectedBatch: LiveData<Batch?> = _selectedBatch

    fun createBatch(breed: String, startDate: Long) {
        if (breed.isBlank()) {
            _uiState.value = BatchUiState.Error("Please enter the silkworm breed name.")
            return
        }

        _uiState.value = BatchUiState.Loading

        viewModelScope.launch {
            try {
                val batch = Batch(
                    breed = breed.trim(),
                    startDate = startDate,
                    currentInstar = 1,
                    status = "ACTIVE"
                )
                val batchId = repository.insertBatch(batch)
                _uiState.value = BatchUiState.Success(batchId)
            } catch (e: Exception) {
                _uiState.value = BatchUiState.Error("Failed to create batch: ${e.message}")
            }
        }
    }

    fun loadAndSyncBatch(batchId: Long) {
        viewModelScope.launch {
            try {
                val batch = repository.getBatchById(batchId.toInt())
                if (batch != null) {
                    val calculatedInstar = repository.calculateCurrentInstar(batch.startDate)

                    if (calculatedInstar != batch.currentInstar) {
                        repository.updateInstar(batchId.toInt(), calculatedInstar)
                        _selectedBatch.postValue(batch.copy(currentInstar = calculatedInstar))
                    } else {
                        _selectedBatch.postValue(batch)
                    }
                } else {
                    _uiState.postValue(BatchUiState.Error("Batch not found."))
                }
            } catch (e: Exception) {
                _uiState.postValue(BatchUiState.Error("Failed to load batch: ${e.message}"))
            }
        }
    }

    fun completeBatch(batchId: Long) {
        viewModelScope.launch {
            try {
                repository.updateBatchStatus(batchId.toInt(), "COMPLETED")
            } catch (e: Exception) {
                _uiState.postValue(BatchUiState.Error("Failed to complete batch: ${e.message}"))
            }
        }
    }

    fun deleteBatch(batch: Batch) {
        viewModelScope.launch {
            try {
                repository.deleteBatch(batch)
            } catch (e: Exception) {
                _uiState.postValue(BatchUiState.Error("Failed to delete batch: ${e.message}"))
            }
        }
    }

    fun isHarvestReady(batch: Batch): Boolean {
        return repository.isHarvestReady(batch.startDate)
    }

    fun resetState() {
        _uiState.value = BatchUiState.Idle
    }
}