package com.mihir.notificationtracker.helper

import android.app.Application
import com.mihir.notificationtracker.database.NotificationDatabase

class AppObjectController {

    companion object {
        lateinit var applicationContext: Application
        val appDatabase by lazy { NotificationDatabase.getDatabase(applicationContext) }

    }

}