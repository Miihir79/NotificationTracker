package com.mihir.notificationtracker.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.model.NotifInfo
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {
    var dao = AppObjectController.appDatabase.notifDao()
    val readAllNotification: LiveData<List<NotifInfo>> = dao.getAllNotifs()
    val readATopNotifications: LiveData<List<NotifInfo>> = dao.getLastFewNotifs()
    val getAllPackageNames: LiveData<List<String>> = dao.getPackageNamesSorted()

    fun addNotification(notification: NotifInfo) {
        viewModelScope.launch {
            dao.addNotifInfo(notification)
        }
    }

}