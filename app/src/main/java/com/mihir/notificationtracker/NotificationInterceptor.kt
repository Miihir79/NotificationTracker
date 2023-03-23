package com.mihir.notificationtracker

import android.app.Notification
import android.content.Context
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.mihir.notificationtracker.database.NotificationDatabase
import com.mihir.notificationtracker.model.NotifInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationInterceptor : NotificationListenerService() {

    private lateinit var context: Context
    private  val dao by lazy { NotificationDatabase.getDatabase(application).notifDao()}

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Log.i("MIHIR_TAG", "notif listner started")
    }

    // reference: https://github.com/Chagall/notification-listener-service-example
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notifInfo = NotifInfo(0,
            sbn?.packageName ?: "",
            sbn?.notification?.extras?.getString(Notification.EXTRA_TITLE) ?: "",
            sbn?.notification?.extras?.getString(Notification.EXTRA_TEXT) ?: "",
            sbn?.postTime ?: 0
        )
        scope.launch {
            dao.addNotifInfo(notifInfo)
        }
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}