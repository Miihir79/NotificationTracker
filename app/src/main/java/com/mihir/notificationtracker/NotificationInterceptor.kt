package com.mihir.notificationtracker

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.mihir.notificationtracker.database.NotificationDatabase
import com.mihir.notificationtracker.helper.logThis
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
        logThis("notif listner started")
    }

    // reference: https://github.com/Chagall/notification-listener-service-example
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        
        // Only track if it has a title and text (most notifications we care about)
        val title = notification.extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = notification.extras?.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        
        if (title.isEmpty() && text.isEmpty()) return

        val notifInfo = NotifInfo(0,
            sbn.packageName ?: "",
            title,
            text,
            sbn.postTime
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