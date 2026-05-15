package com.reshmenamma.pride.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "climate_readings",
    indices = [Index(value = ["batchId", "readingDate", "timeSlot"], unique = true)]
)
data class ClimateReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Int,
    val readingDate: String,
    val timeSlot: String,
    val temperature: Float,
    val humidity: Float,
    val feedGiven: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)