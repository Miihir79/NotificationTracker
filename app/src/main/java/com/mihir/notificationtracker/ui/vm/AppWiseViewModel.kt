package com.mihir.notificationtracker.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.model.NotifInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AppWiseViewModel(application: Application) : AndroidViewModel(application) {

    var dao = AppObjectController.appDatabase.notifDao()
    val observeAppData = MutableLiveData<List<NotifInfo>>()
    val totalNotificationCount = MutableLiveData<Int>()
    val todayNotificationCount = MutableLiveData<Int>()

    fun getAppNotifs(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.getAppNotification(packageName)
            observeAppData.postValue(res)
        }
    }

    fun getAppNotifCount(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.getAppNotifCount(packageName)
            totalNotificationCount.postValue(res)
        }
    }

    fun getAppNotifCountToday(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            val todayStartInMillis = c.timeInMillis
            val res = dao.getAppNotifCountToday(packageName, todayStartInMillis)
            todayNotificationCount.postValue(res)
        }
    }

}