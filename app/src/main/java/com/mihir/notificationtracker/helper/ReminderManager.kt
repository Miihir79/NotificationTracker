package com.mihir.notificationtracker.helper

import android.content.Context
import androidx.work.*
import com.mihir.notificationtracker.model.ImportantContact
import com.mihir.notificationtracker.worker.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderManager {

    fun scheduleReminder(context: Context, category: String) {
        val prefs = context.getSharedPreferences("notif_tracker_prefs", Context.MODE_PRIVATE)
        val timeKey = if (category == ImportantContact.CATEGORY_BUSINESS) "business_time" else "personal_time"
        val defaultTime = if (category == ImportantContact.CATEGORY_BUSINESS) "20:00" else "21:00"
        val timeStr = prefs.getString(timeKey, defaultTime) ?: defaultTime
        
        val parts = timeStr.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        
        val data = workDataOf("CATEGORY" to category)
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("REMINDER_$category")
            .build()
            
        WorkManager.getInstance(context).enqueueUniqueWork(
            "REMINDER_$category",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
