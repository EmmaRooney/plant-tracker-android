package com.example.planttracker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.planttracker.R
import com.example.planttracker.view.ui.MainActivity

class NotificationUtil {

    companion object {

        private const val CHANNEL_ID = "PLANT_WATER"

        fun createNotificationChannel(context: Context, channelName: String, channelDesc: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // create the notification channel
                val channel = NotificationChannel(
                    CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = channelDesc
                }
                // register the channel with the system
                val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun createNotification(context: Context, title: String, msg: String): NotificationCompat.Builder {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.plant)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

    }

}