package com.reshmenamma.pride.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reshmenamma.pride.data.entity.ClimateReadingEntity
import com.reshmenamma.pride.data.model.ClimateLog
import com.reshmenamma.pride.data.model.Harvest
import com.reshmenamma.pride.data.repository.ReshmeRepository
import com.reshmenamma.pride.util.Constants
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class ClimateUiState {
    object Idle : ClimateUiState()
    object Loading : ClimateUiState()
    data class AdviceReady(
        val dialStatus: String,
        val advice: String,
        val temperature: Float,
        val humidity: Int,
        val instar: Int
    ) : ClimateUiState()
    data class Error(val message: String) : ClimateUiState()
    object HarvestSaved : ClimateUiState()
}

class ClimateViewModel(private val repository: ReshmeRepository) : ViewModel() {

    private val _uiState = MutableLiveData<ClimateUiState>(ClimateUiState.Idle)
    val uiState: LiveData<ClimateUiState> = _uiState

    private val _todayLogCount = MutableLiveData(0)
    val todayLogCount: LiveData<Int> = _todayLogCount

    private val _existingHarvest = MutableLiveData<Harvest?>()
    val existingHarvest: LiveData<Harvest?> = _existingHarvest

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private suspend fun getAiAdvice(temp: Float, humidity: Int, instar: Int, status: String): String {
        val config = Constants.INSTAR_STAGES[instar]
        val prompt = """
        You are an expert sericulture advisor for Karnataka silk farmers.
        Silkworm stage: ${config?.name ?: "Instar $instar"}
        Temperature: ${temp}°C (ideal: ${config?.idealTempMin}–${config?.idealTempMax}°C)
        Humidity: $humidity% (ideal: ${config?.idealHumidityMin}–${config?.idealHumidityMax}%)
        Status: $status
        Give 2-3 sentence practical advice in simple English. Be direct and actionable.
    """.trimIndent()

        val request = com.reshmenamma.pride.data.api.GeminiRequest(
            contents = listOf(
                com.reshmenamma.pride.data.api.GeminiContent(
                    parts = listOf(
                        com.reshmenamma.pride.data.api.GeminiPart(text = prompt)
                    )
                )
            )
        )

        val response = com.reshmenamma.pride.data.api.GeminiClient.apiService
            .generateContent(apiKey = Constants.GEMINI_API_KEY, request = request)

        return if (response.isSuccessful) {
            response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: getFallbackAdvice(temp, humidity, instar, status, Constants.INSTAR_STAGES[instar])
        } else {
            getFallbackAdvice(temp, humidity, instar, status, Constants.INSTAR_STAGES[instar])
        }
    }

    private fun getFallbackAdvice(
        temp: Float,
        humidity: Int,
        instar: Int,
        status: String,
        config: com.reshmenamma.pride.util.InstarConfig?
    ): String {
        return when (status) {
            Constants.STATUS_GREEN ->
                "✅ Conditions are perfect for ${config?.name}! Keep monitoring 3 times daily."

            Constants.STATUS_ORANGE -> when {
                temp > (config?.idealTempMax ?: 28f) ->
                    "⚠️ Temperature slightly high. Open windows on the shaded side. Avoid direct sunlight on trays."
                temp < (config?.idealTempMin ?: 26f) ->
                    "⚠️ Temperature slightly low. Close windows and keep worms away from cold drafts."
                humidity > (config?.idealHumidityMax ?: 90) ->
                    "⚠️ Humidity high. Increase ventilation and remove wet surfaces."
                else ->
                    "⚠️ Humidity low. Lightly sprinkle water on floor. Do not spray on worms."
            }

            else -> when {
                temp > (config?.cautionTempMax ?: 30f) ->
                    "🚨 DANGER! Temperature critically high. Open ALL windows, spread wet gunny bags NOW!"
                temp < (config?.cautionTempMin ?: 24f) ->
                    "🚨 DANGER! Temperature critically low. Heat room immediately. Cover trays with cloth."
                humidity > (config?.cautionHumidityMax ?: 95) ->
                    "🚨 DANGER! Humidity too high — fungal risk! Open all vents, remove ALL wet materials."
                else ->
                    "🚨 DANGER! Humidity critically low. Spread wet gunny bags on walls and floor now."
            }
        }
    }

    fun getLogsForBatch(batchId: Int) = repository.getLogsForBatch(batchId)

    fun getReadingHistoryForBatch(batchId: Int): LiveData<List<ClimateReadingEntity>> {
        return repository.getReadingsForBatch(batchId)
    }

    fun loadHarvestForBatch(batchId: Int) {
        viewModelScope.launch {
            _existingHarvest.postValue(repository.getHarvestForBatch(batchId))
        }
    }

    fun validateInputs(tempStr: String, humidityStr: String): String? {
        val temp = tempStr.toFloatOrNull()
            ?: return "Please enter a valid temperature (e.g., 27.5)"
        val humidity = humidityStr.toIntOrNull()
            ?: return "Please enter a valid humidity percentage (e.g., 85)"

        if (temp < Constants.TEMP_MIN || temp > Constants.TEMP_MAX) {
            return "Temperature must be between ${Constants.TEMP_MIN}°C and ${Constants.TEMP_MAX}°C"
        }

        if (humidity < Constants.HUMIDITY_MIN || humidity > Constants.HUMIDITY_MAX) {
            return "Humidity must be between ${Constants.HUMIDITY_MIN}% and ${Constants.HUMIDITY_MAX}%"
        }

        return null
    }

    fun submitClimateReading(
        batchId: Int,
        tempStr: String,
        humidityStr: String,
        timeOfDay: String,
        instar: Int
    ) {
        val errorMsg = validateInputs(tempStr, humidityStr)
        if (errorMsg != null) {
            _uiState.value = ClimateUiState.Error(errorMsg)
            return
        }

        val temperature = tempStr.toFloat()
        val humidity = humidityStr.toInt()
        val todayDate = getTodayDateString()

        _uiState.value = ClimateUiState.Loading

        viewModelScope.launch {
            try {
                val todayCount = repository.getEntryCountForDate(batchId, todayDate)
                if (todayCount >= 3) {
                    _uiState.postValue(
                        ClimateUiState.Error(
                            "You have already entered 3 readings today. Come back tomorrow!"
                        )
                    )
                    return@launch
                }

                val existingSlotReading = repository.getReadingForSlot(batchId, todayDate, timeOfDay)
                if (existingSlotReading != null) {
                    _uiState.postValue(
                        ClimateUiState.Error("$timeOfDay reading has been already entered for today.")
                    )
                    return@launch
                }

                val config = Constants.INSTAR_STAGES[instar]

                val dialStatus = when {
                    config != null &&
                            temperature >= config.idealTempMin &&
                            temperature <= config.idealTempMax &&
                            humidity >= config.idealHumidityMin &&
                            humidity <= config.idealHumidityMax -> Constants.STATUS_GREEN

                    config != null &&
                            temperature >= config.cautionTempMin &&
                            temperature <= config.cautionTempMax &&
                            humidity >= config.cautionHumidityMin &&
                            humidity <= config.cautionHumidityMax -> Constants.STATUS_ORANGE

                    else -> Constants.STATUS_RED
                }

                val advice = try {
                    getAiAdvice(temperature, humidity, instar, dialStatus)
                } catch (e: Exception) {
                    getFallbackAdvice(temperature, humidity, instar, dialStatus, config)
                }

                val log = ClimateLog(
                    batchId = batchId,
                    timeOfDay = timeOfDay,
                    temperature = temperature,
                    humidity = humidity,
                    dialStatus = dialStatus,
                    aiAdvice = advice,
                    instarStage = instar,
                    feedGiven = "",
                    notes = ""
                )

                repository.insertClimateLog(log)

                repository.insertClimateReading(
                    ClimateReadingEntity(
                        batchId = batchId,
                        readingDate = todayDate,
                        timeSlot = timeOfDay,
                        temperature = temperature,
                        humidity = humidity.toFloat(),
                        feedGiven = "",
                        notes = ""
                    )
                )

                _todayLogCount.postValue(repository.getEntryCountForDate(batchId, todayDate))

                _uiState.postValue(
                    ClimateUiState.AdviceReady(
                        dialStatus = dialStatus,
                        advice = advice,
                        temperature = temperature,
                        humidity = humidity,
                        instar = instar
                    )
                )
            } catch (e: Exception) {
                _uiState.postValue(
                    ClimateUiState.Error("Something went wrong: ${e.message}")
                )
            }
        }
    }

    fun saveHarvest(batchId: Int, yieldKg: Float, notes: String) {
        viewModelScope.launch {
            try {
                val harvest = Harvest(
                    batchId = batchId,
                    cocoonYieldKg = yieldKg,
                    notes = notes
                )
                repository.insertHarvest(harvest)
                repository.updateBatchStatus(batchId, "COMPLETED")
                _uiState.postValue(ClimateUiState.HarvestSaved)
            } catch (e: Exception) {
                _uiState.postValue(
                    ClimateUiState.Error("Failed to save harvest: ${e.message}")
                )
            }
        }
    }

    fun loadTodayCount(batchId: Int) {
        viewModelScope.launch {
            val todayDate = getTodayDateString()
            _todayLogCount.postValue(repository.getEntryCountForDate(batchId, todayDate))
        }
    }

    fun resetState() {
        _uiState.value = ClimateUiState.Idle
    }
}