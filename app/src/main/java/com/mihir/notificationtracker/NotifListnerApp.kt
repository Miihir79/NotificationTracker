package com.mihir.notificationtracker

import android.app.Application
import com.mihir.notificationtracker.helper.AppObjectController

class NotifListnerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppObjectController.applicationContext = this
    }

}