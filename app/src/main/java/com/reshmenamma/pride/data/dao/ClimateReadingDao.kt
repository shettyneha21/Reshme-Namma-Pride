package com.reshmenamma.pride.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reshmenamma.pride.data.entity.ClimateReadingEntity

@Dao
interface ClimateReadingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: ClimateReadingEntity)

    @Query("SELECT COUNT(*) FROM climate_readings WHERE batchId = :batchId AND readingDate = :date")
    suspend fun getEntryCountForDate(batchId: Int, date: String): Int

    @Query("SELECT * FROM climate_readings WHERE batchId = :batchId AND readingDate = :date AND timeSlot = :timeSlot LIMIT 1")
    suspend fun getReadingForSlot(batchId: Int, date: String, timeSlot: String): ClimateReadingEntity?

    @Query("SELECT * FROM climate_readings WHERE batchId = :batchId ORDER BY readingDate DESC, timeSlot ASC")
    fun getReadingsForBatch(batchId: Int): LiveData<List<ClimateReadingEntity>>

    @Query("SELECT * FROM climate_readings WHERE batchId = :batchId AND readingDate = :date ORDER BY timeSlot ASC")
    suspend fun getReadingsForDate(batchId: Int, date: String): List<ClimateReadingEntity>
}