package com.mihir.notificationtracker.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.model.NotifInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppWiseViewModel(application: Application) : AndroidViewModel(application) {

    var dao = AppObjectController.appDatabase.notifDao()
    val observeAppData = MutableLiveData<List<NotifInfo>>()

    fun getAppNotifs(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = dao.getAppNotification(packageName)
            observeAppData.postValue(res)
        }
    }

}