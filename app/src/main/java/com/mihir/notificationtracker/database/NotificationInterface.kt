package com.mihir.notificationtracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mihir.notificationtracker.model.NotifInfo

@Dao
interface NotificationInterface {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotifInfo(beer: NotifInfo)

    @Query("SELECT * FROM notif_data ORDER BY time DESC")
    fun getAllNotifs(): LiveData<List<NotifInfo>>

    @Query("SELECT * FROM notif_data ORDER BY time DESC LIMIT 10")
    fun getLastFewNotifs(): LiveData<List<NotifInfo>>

    @Query("SELECT * FROM notif_data ORDER BY packageName ASC")
    fun getNotifSortedByPackageName(): LiveData<List<NotifInfo>>

    @Query("SELECT * FROM notif_data WHERE packageName = :packageName ORDER BY time DESC")
    fun getAppNotification(packageName: String): List<NotifInfo>

}