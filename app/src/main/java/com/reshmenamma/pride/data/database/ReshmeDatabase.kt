package com.reshmenamma.pride.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.reshmenamma.pride.data.dao.BatchDao
import com.reshmenamma.pride.data.dao.ClimateLogDao
import com.reshmenamma.pride.data.dao.ClimateReadingDao
import com.reshmenamma.pride.data.dao.HarvestDao
import com.reshmenamma.pride.data.entity.ClimateReadingEntity
import com.reshmenamma.pride.data.model.Batch
import com.reshmenamma.pride.data.model.ClimateLog
import com.reshmenamma.pride.data.model.Harvest

@Database(
    entities = [Batch::class, ClimateLog::class, Harvest::class, ClimateReadingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ReshmeDatabase : RoomDatabase() {

    abstract fun batchDao(): BatchDao
    abstract fun climateLogDao(): ClimateLogDao
    abstract fun climateReadingDao(): ClimateReadingDao
    abstract fun harvestDao(): HarvestDao

    companion object {
        @Volatile
        private var INSTANCE: ReshmeDatabase? = null

        fun getDatabase(context: Context): ReshmeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReshmeDatabase::class.java,
                    "reshme_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}