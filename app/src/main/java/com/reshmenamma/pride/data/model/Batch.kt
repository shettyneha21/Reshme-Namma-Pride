package com.reshmenamma.pride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batch")
data class Batch(
    @PrimaryKey(autoGenerate = true)
    val batchId: Int = 0,
    val breed: String,
    val startDate: Long,
    val currentInstar: Int = 1,
    val status: String = "ACTIVE"
)