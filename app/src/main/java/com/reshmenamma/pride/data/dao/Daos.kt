package com.reshmenamma.pride.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.reshmenamma.pride.data.model.Batch
import com.reshmenamma.pride.data.model.ClimateLog
import com.reshmenamma.pride.data.model.Harvest

// ─── Batch DAO ────────────────────────────────────────────────────────────────
@Dao
interface BatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: Batch): Long

    @Update
    suspend fun updateBatch(batch: Batch)

    @Delete
    suspend fun deleteBatch(batch: Batch)

    @Query("DELETE FROM batch WHERE batchId = :batchId")
    suspend fun deleteBatchById(batchId: Int)

    @Query("SELECT * FROM batch WHERE status = 'ACTIVE' ORDER BY startDate DESC")
    fun getActiveBatches(): LiveData<List<Batch>>

    @Query("SELECT * FROM batch ORDER BY startDate DESC")
    fun getAllBatches(): LiveData<List<Batch>>

    @Query("SELECT * FROM batch WHERE batchId = :batchId")
    suspend fun getBatchById(batchId: Int): Batch?

    @Query("UPDATE batch SET currentInstar = :instar WHERE batchId = :batchId")
    suspend fun updateInstar(batchId: Int, instar: Int)

    @Query("UPDATE batch SET status = :status WHERE batchId = :batchId")
    suspend fun updateStatus(batchId: Int, status: String)
}

// ─── Climate Log DAO ──────────────────────────────────────────────────────────
@Dao
interface ClimateLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ClimateLog): Long

    @Query("SELECT * FROM climate_log WHERE batchId = :batchId ORDER BY logTime DESC")
    fun getLogsForBatch(batchId: Int): LiveData<List<ClimateLog>>

    @Query("SELECT * FROM climate_log WHERE batchId = :batchId ORDER BY logTime DESC LIMIT 1")
    suspend fun getLatestLogForBatch(batchId: Int): ClimateLog?

    @Query("DELETE FROM climate_log")
    suspend fun deleteAllLogs()

    @Query("""
        SELECT COUNT(*) FROM climate_log 
        WHERE batchId = :batchId 
        AND date(logTime / 1000, 'unixepoch') = date('now')
    """)
    suspend fun getTodayLogCount(batchId: Int): Int

    @Query("DELETE FROM climate_log WHERE batchId = :batchId")
    suspend fun deleteLogsForBatch(batchId: Int)
}

// ─── Harvest DAO ──────────────────────────────────────────────────────────────
@Dao
interface HarvestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvest(harvest: Harvest): Long

    @Delete
    suspend fun deleteHarvest(harvest: Harvest)

    @Query("DELETE FROM harvest WHERE batchId = :batchId")
    suspend fun deleteHarvestForBatch(batchId: Int)

    @Query("DELETE FROM harvest")
    suspend fun deleteAllHarvests()

    @Query("SELECT * FROM harvest WHERE batchId = :batchId LIMIT 1")
    suspend fun getHarvestForBatch(batchId: Int): Harvest?

    @Query("SELECT * FROM harvest ORDER BY harvestDate DESC")
    fun getAllHarvests(): LiveData<List<Harvest>>
}