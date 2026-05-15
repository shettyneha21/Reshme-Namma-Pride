package com.reshmenamma.pride.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.reshmenamma.pride.R

class HarvestTimerWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val batchId = inputData.getLong("batch_id", -1L)
        val breed = inputData.getString("breed") ?: "your batch"
        sendHarvestNotification(batchId, breed)
        return Result.success()
    }

    private fun sendHarvestNotification(batchId: Long, breed: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Harvest Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when cocoons are ready for harvest"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_harvest)
            .setContentTitle("🌿 Cocoon Harvest Ready!")
            .setContentText("Your $breed batch is ready! Transfer cocoons to spinning trays now.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Your $breed silkworm batch has completed the rearing cycle. It's time to transfer your cocoons to the spinning trays for silk reeling. Don't delay — over-ripened cocoons reduce silk quality!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(batchId.toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "reshme_harvest_channel"
    }
}
