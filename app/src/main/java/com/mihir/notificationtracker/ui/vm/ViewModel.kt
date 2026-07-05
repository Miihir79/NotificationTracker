package com.mihir.notificationtracker.ui.vm

import android.app.Application
import androidx.lifecycle.*
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.model.ImportantContact
import com.mihir.notificationtracker.model.NotifInfo
import kotlinx.coroutines.launch
import java.util.*

class ViewModel(application: Application) : AndroidViewModel(application) {
    var dao = AppObjectController.appDatabase.notifDao()
    
    private val _selectedPackageName = MutableLiveData<String?>(null)
    private val _timeRange = MutableLiveData<Pair<Long, Long>>(getDefaultTimeRange())
    private val _selectedHour = MutableLiveData<Int?>(null)

    val selectedPackageName: LiveData<String?> = _selectedPackageName
    val timeRange: LiveData<Pair<Long, Long>> = _timeRange
    val selectedHour: LiveData<Int?> = _selectedHour

    val filteredNotificationTimes: LiveData<List<Long>> = _selectedPackageName.switchMap { pkg ->
        _timeRange.switchMap { range ->
            dao.getFilteredNotificationTimes(pkg, range.first, range.second)
        }
    }

    val filteredTopApps: LiveData<List<com.mihir.notificationtracker.database.AppUsageStats>> = _timeRange.switchMap { range ->
        _selectedHour.switchMap { hour ->
            if (hour == null) {
                dao.getFilteredTopApps(range.first, range.second)
            } else {
                val hourStr = String.format(Locale.getDefault(), "%02d", hour)
                dao.getTopAppsForHour(range.first, range.second, hourStr)
            }
        }
    }

    val allUniquePackageNames: LiveData<List<String>> = dao.getAllUniquePackageNames()

    val readAllNotification: LiveData<List<NotifInfo>> = dao.getAllNotifs()
    val getAllPackageNames: LiveData<List<String>> = dao.getPackageNamesSorted()
    val readATopNotifications: LiveData<List<NotifInfo>> = dao.getLastFewNotifs()
    val totalNotifCount: LiveData<Int> = dao.getTotalNotificationCount()
    val firstNotifTime: LiveData<Long?> = dao.getFirstNotificationTime()

    // Important Contacts & Notifications
    val importantContacts: LiveData<List<ImportantContact>> = dao.getAllImportantContacts()
    
    fun getImportantContactsByCategory(category: String): LiveData<List<ImportantContact>> = 
        dao.getImportantContactsByCategory(category)

    val importantNotifications: LiveData<List<NotifInfo>> = dao.getNotificationsFromImportantContacts()

    fun getImportantNotificationsByCategory(category: String): LiveData<List<NotifInfo>> =
        dao.getNotificationsFromImportantContactsByCategory(category)

    val uniquePackageNames: LiveData<List<String>> = dao.getUniquePackageNamesSorted()

    fun addImportantContact(name: String, packageName: String, category: String) {
        viewModelScope.launch {
            dao.insertImportantContact(ImportantContact(0, name, packageName, category))
        }
    }

    fun removeImportantContact(contact: ImportantContact) {
        viewModelScope.launch {
            dao.deleteImportantContact(contact)
        }
    }

    fun getUniqueContacts(packageName: String): LiveData<List<String>> = dao.getUniqueContactsFromApp(packageName)

    fun setSelectedPackage(packageName: String?) {
        _selectedPackageName.value = if (packageName == "All Apps") null else packageName
    }

    fun setTimeRange(start: Long, end: Long) {
        _timeRange.value = Pair(start, end)
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            dao.clearAllNotifications()
        }
    }

    fun deleteOldNotifications(days: Int) {
        val cutoff = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        viewModelScope.launch {
            dao.deleteNotificationsOlderThan(cutoff)
        }
    }

    fun setSelectedHour(hour: Int?) {
        _selectedHour.value = hour
    }

    private fun getDefaultTimeRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }

    fun addNotification(notification: NotifInfo) {
        viewModelScope.launch {
            dao.addNotifInfo(notification)
        }
    }

}