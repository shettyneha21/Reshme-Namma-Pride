package com.reshmenamma.pride.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.reshmenamma.pride.data.dao.BatchDao
import com.reshmenamma.pride.data.dao.ClimateLogDao
import com.reshmenamma.pride.data.dao.ClimateReadingDao
import com.reshmenamma.pride.data.dao.HarvestDao
import com.reshmenamma.pride.data.entity.ClimateReadingEntity
import com.reshmenamma.pride.data.model.Batch
import com.reshmenamma.pride.data.model.BatchHistoryItem
import com.reshmenamma.pride.data.model.ClimateLog
import com.reshmenamma.pride.data.model.Harvest
import java.util.concurrent.TimeUnit

class ReshmeRepository(
    private val batchDao: BatchDao,
    private val climateLogDao: ClimateLogDao,
    private val climateReadingDao: ClimateReadingDao,
    private val harvestDao: HarvestDao
) {

    fun getAllBatches(): LiveData<List<Batch>> = batchDao.getAllBatches()

    fun getBatchHistory(): LiveData<List<BatchHistoryItem>> {
        val result = MediatorLiveData<List<BatchHistoryItem>>()

        val batchesLiveData = batchDao.getAllBatches()
        val harvestsLiveData = harvestDao.getAllHarvests()

        fun update() {
            val batches = batchesLiveData.value ?: emptyList()
            val harvestMap = harvestsLiveData.value?.associateBy { it.batchId } ?: emptyMap()

            val historyItems = batches.map { batch ->
                val harvest = harvestMap[batch.batchId]
                BatchHistoryItem(
                    batchId = batch.batchId,
                    breed = batch.breed,
                    startDate = batch.startDate,
                    status = batch.status,
                    cocoonYieldKg = harvest?.cocoonYieldKg,
                    notes = harvest?.notes,
                    harvestDate = harvest?.harvestDate
                )
            }

            result.value = historyItems
        }

        result.addSource(batchesLiveData) { update() }
        result.addSource(harvestsLiveData) { update() }

        return result
    }

    suspend fun insertBatch(batch: Batch): Long = batchDao.insertBatch(batch)

    suspend fun updateBatch(batch: Batch) = batchDao.updateBatch(batch)

    suspend fun deleteBatch(batch: Batch) = batchDao.deleteBatch(batch)

    suspend fun deleteBatchById(batchId: Int) = batchDao.deleteBatchById(batchId)

    suspend fun getBatchById(batchId: Int): Batch? = batchDao.getBatchById(batchId)

    suspend fun updateInstar(batchId: Int, instar: Int) =
        batchDao.updateInstar(batchId, instar)

    suspend fun updateBatchStatus(batchId: Int, status: String) =
        batchDao.updateStatus(batchId, status)

    fun getLogsForBatch(batchId: Int): LiveData<List<ClimateLog>> =
        climateLogDao.getLogsForBatch(batchId)

    suspend fun insertClimateLog(log: ClimateLog): Long =
        climateLogDao.insertLog(log)

    suspend fun getLatestLogForBatch(batchId: Int): ClimateLog? =
        climateLogDao.getLatestLogForBatch(batchId)

    suspend fun getTodayLogCount(batchId: Int): Int =
        climateLogDao.getTodayLogCount(batchId)

    suspend fun insertClimateReading(reading: ClimateReadingEntity) =
        climateReadingDao.insertReading(reading)

    suspend fun getEntryCountForDate(batchId: Int, date: String): Int =
        climateReadingDao.getEntryCountForDate(batchId, date)

    suspend fun getReadingForSlot(
        batchId: Int,
        date: String,
        timeSlot: String
    ): ClimateReadingEntity? =
        climateReadingDao.getReadingForSlot(batchId, date, timeSlot)

    fun getReadingsForBatch(batchId: Int): LiveData<List<ClimateReadingEntity>> =
        climateReadingDao.getReadingsForBatch(batchId)

    suspend fun getReadingsForDate(batchId: Int, date: String): List<ClimateReadingEntity> =
        climateReadingDao.getReadingsForDate(batchId, date)

    suspend fun insertHarvest(harvest: Harvest): Long =
        harvestDao.insertHarvest(harvest)

    fun getAllHarvests(): LiveData<List<Harvest>> =
        harvestDao.getAllHarvests()

    suspend fun getHarvestForBatch(batchId: Int): Harvest? =
        harvestDao.getHarvestForBatch(batchId)

    suspend fun deleteHarvestForBatch(batchId: Int) =
        harvestDao.deleteHarvestForBatch(batchId)

    fun calculateCurrentInstar(startDate: Long): Int {
        val daysSinceStart = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - startDate
        ).toInt()

        return when {
            daysSinceStart < 3 -> 1
            daysSinceStart < 6 -> 2
            daysSinceStart < 9 -> 3
            daysSinceStart < 12 -> 4
            daysSinceStart < 15 -> 5
            else -> 6
        }
    }

    fun isHarvestReady(startDate: Long): Boolean {
        val daysSinceStart = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - startDate
        )
        return daysSinceStart >= 15
    }
}