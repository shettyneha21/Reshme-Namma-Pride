package com.reshmenamma.pride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "harvest")
data class Harvest(
    @PrimaryKey(autoGenerate = true)
    val harvestId: Int = 0,
    val batchId: Int,
    val cocoonYieldKg: Float,
    val notes: String = "",
    val harvestDate: Long = System.currentTimeMillis()
)