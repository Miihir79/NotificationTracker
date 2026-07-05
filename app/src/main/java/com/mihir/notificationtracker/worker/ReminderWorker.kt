package com.mihir.notificationtracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.database.NotificationDatabase
import com.mihir.notificationtracker.helper.ReminderManager
import com.mihir.notificationtracker.ui.screens.MainActivity

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val category = inputData.getString("CATEGORY") ?: return Result.failure()
        val dao = NotificationDatabase.getDatabase(applicationContext).notifDao()
        val prefs = applicationContext.getSharedPreferences("notif_tracker_prefs", Context.MODE_PRIVATE)

        val lastReminderKey = "last_reminder_$category"
        val lastReminderTime = prefs.getLong(lastReminderKey, 0L)

        val newNotifs = dao.getNewImportantNotifications(category, lastReminderTime)

        if (newNotifs.isNotEmpty()) {
            showNotification(category, newNotifs.size)
        }

        prefs.edit().putLong(lastReminderKey, System.currentTimeMillis()).apply()

        // Schedule next reminder for tomorrow
        ReminderManager.scheduleReminder(applicationContext, category)

        return Result.success()
    }

    private fun showNotification(category: String, count: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = if (category == "BUSINESS") "business_important_channel" else "personal_important_channel"
        val channelName = if (category == "BUSINESS") "Business Reminders" else "Personal Reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "importantMessagesFragment")
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, category.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)

        val title = if (category == "BUSINESS") "Business Messages Await" else "Family & Friends Messages"
        val text = "You have $count important $category messages to read."

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(category.hashCode(), notification)
    }
}
