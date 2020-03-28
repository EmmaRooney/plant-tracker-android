package com.example.planttracker.util

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.planttracker.model.data.PlantDatabase
import java.util.*

class WaterCheckerWorker(private val appContext: Context, workerParameters: WorkerParameters)
    : Worker(appContext, workerParameters) {

    override fun doWork(): Result {

        // decrease days to water by 1
        PlantDatabase.getInstance(appContext).plantDao().decreaseDaysToWater()

        // check if any plants need watering and if so, send a notification
        if (PlantDatabase.getInstance(appContext).plantDao().searchPlantsNeedWatering() > 0) {
            // send notification
            val notificationID = 1
            with(NotificationManagerCompat.from(appContext)) {
                notify(notificationID, NotificationUtil.createNotification(appContext,"A plant needs watering!", "Water now!").build())
            }
        }

        return Result.success()
    }

}