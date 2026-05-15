package com.reshmenamma.pride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "climate_log")
data class ClimateLog(
    @PrimaryKey(autoGenerate = true)
    val logId: Int = 0,
    val batchId: Int,
    val timeOfDay: String,
    val temperature: Float,
    val humidity: Int,
    val dialStatus: String,
    val aiAdvice: String,
    val instarStage: Int,
    val feedGiven: String = "",
    val notes: String = "",
    val logTime: Long = System.currentTimeMillis()
)