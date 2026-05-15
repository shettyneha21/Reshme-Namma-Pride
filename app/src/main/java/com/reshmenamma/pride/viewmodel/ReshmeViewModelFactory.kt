package com.reshmenamma.pride.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.reshmenamma.pride.data.database.ReshmeDatabase
import com.reshmenamma.pride.data.repository.ReshmeRepository

class ReshmeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = ReshmeDatabase.getDatabase(context)
        val repository = ReshmeRepository(
            batchDao = db.batchDao(),
            climateLogDao = db.climateLogDao(),
            climateReadingDao = db.climateReadingDao(),
            harvestDao = db.harvestDao()
        )

        return when {
            modelClass.isAssignableFrom(BatchViewModel::class.java) ->
                BatchViewModel(repository) as T
            modelClass.isAssignableFrom(ClimateViewModel::class.java) ->
                ClimateViewModel(repository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}