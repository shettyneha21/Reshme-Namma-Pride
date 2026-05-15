package com.reshmenamma.pride.worker

import android.content.Context
import androidx.work.*
import com.reshmenamma.pride.util.Constants
import java.util.concurrent.TimeUnit

object HarvestScheduler {

    fun scheduleHarvestAlert(context: Context, batchId: Long, breed: String, startDateMillis: Long) {
        val harvestDateMillis = startDateMillis + (Constants.TOTAL_REARING_DAYS * 24 * 60 * 60 * 1000L)
        val delayMillis = harvestDateMillis - System.currentTimeMillis()

        if (delayMillis <= 0) return // Harvest already due

        val inputData = workDataOf(
            "batch_id" to batchId,
            "breed" to breed
        )

        val harvestWork = OneTimeWorkRequestBuilder<HarvestTimerWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("harvest_$batchId")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "harvest_timer_$batchId",
                ExistingWorkPolicy.REPLACE,
                harvestWork
            )
    }

    fun cancelHarvestAlert(context: Context, batchId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("harvest_timer_$batchId")
    }
}
