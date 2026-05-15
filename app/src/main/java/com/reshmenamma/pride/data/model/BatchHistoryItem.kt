package com.reshmenamma.pride.data.model

data class BatchHistoryItem(
    val batchId: Int,
    val breed: String,
    val startDate: Long,
    val status: String,
    val cocoonYieldKg: Float? = null,
    val notes: String? = null,
    val harvestDate: Long? = null
)