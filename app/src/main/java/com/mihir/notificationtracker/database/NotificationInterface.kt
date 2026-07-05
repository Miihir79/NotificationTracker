package com.mihir.notificationtracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mihir.notificationtracker.model.ImportantContact
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

    @Query("SELECT packageName FROM notif_data ORDER BY packageName ASC")
    fun getPackageNamesSorted(): LiveData<List<String>>

    @Query("SELECT * FROM notif_data WHERE packageName = :packageName AND (:contactName IS NULL OR heading = :contactName) AND (:startTime IS NULL OR time >= :startTime) AND (:endTime IS NULL OR time <= :endTime) ORDER BY time DESC")
    fun getNotificationsFiltered(packageName: String, contactName: String?, startTime: Long?, endTime: Long?): List<NotifInfo>

    @Query("SELECT COUNT(id) FROM notif_data WHERE packageName = :packageName")
    fun getAppNotifCount(packageName: String): Int

    @Query("SELECT COUNT(id) FROM notif_data WHERE packageName = :packageName AND time > :todayStartTimeStamp")
    fun getAppNotifCountToday(packageName: String, todayStartTimeStamp: Long): Int

    @Query("SELECT packageName, COUNT(id) as count FROM notif_data GROUP BY packageName ORDER BY count DESC LIMIT 5")
    fun getTopApps(): LiveData<List<AppUsageStats>>

    @Query("SELECT time FROM notif_data")
    fun getAllNotificationTimes(): LiveData<List<Long>>

    @Query("SELECT COUNT(id) FROM notif_data WHERE time >= :startTime")
    fun getNotificationCountSince(startTime: Long): LiveData<Int>

    @Query("SELECT COUNT(id) FROM notif_data")
    fun getTotalNotificationCount(): LiveData<Int>

    @Query("SELECT time FROM notif_data ORDER BY time ASC LIMIT 1")
    fun getFirstNotificationTime(): LiveData<Long?>

    @Query("SELECT time FROM notif_data WHERE (:packageName IS NULL OR packageName = :packageName) AND (time >= :startTime AND time <= :endTime)")
    fun getFilteredNotificationTimes(packageName: String?, startTime: Long, endTime: Long): LiveData<List<Long>>

    @Query("SELECT packageName, COUNT(id) as count FROM notif_data WHERE (time >= :startTime AND time <= :endTime) GROUP BY packageName ORDER BY count DESC LIMIT 5")
    fun getFilteredTopApps(startTime: Long, endTime: Long): LiveData<List<AppUsageStats>>

    @Query("SELECT packageName, COUNT(id) as count FROM notif_data WHERE (time >= :startTime AND time <= :endTime) AND (strftime('%H', time/1000, 'unixepoch', 'localtime') = :hour) GROUP BY packageName ORDER BY count DESC LIMIT 5")
    fun getTopAppsForHour(startTime: Long, endTime: Long, hour: String): LiveData<List<AppUsageStats>>

    @Query("SELECT packageName FROM notif_data GROUP BY packageName ORDER BY COUNT(id) DESC")
    fun getAllUniquePackageNames(): LiveData<List<String>>

    @Query("DELETE FROM notif_data")
    suspend fun clearAllNotifications()

    @Query("DELETE FROM notif_data WHERE time < :timestamp")
    suspend fun deleteNotificationsOlderThan(timestamp: Long)

    // Important Contacts DAO
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportantContact(contact: ImportantContact)

    @Delete
    suspend fun deleteImportantContact(contact: ImportantContact)

    @Query("SELECT * FROM important_contacts WHERE category = :category")
    fun getImportantContactsByCategory(category: String): LiveData<List<ImportantContact>>

    @Query("SELECT * FROM important_contacts")
    fun getAllImportantContacts(): LiveData<List<ImportantContact>>

    @Query("SELECT * FROM important_contacts")
    suspend fun getAllImportantContactsSync(): List<ImportantContact>

    @Query("SELECT * FROM important_contacts WHERE contactName = :name AND packageName = :packageName LIMIT 1")
    suspend fun getImportantContact(name: String, packageName: String): ImportantContact?

    @Query("SELECT DISTINCT heading FROM notif_data WHERE packageName = :packageName")
    fun getUniqueContactsFromApp(packageName: String): LiveData<List<String>>

    @Query("""
        SELECT n.* FROM notif_data n 
        INNER JOIN important_contacts c ON n.heading = c.contactName AND n.packageName = c.packageName
        WHERE c.category = :category
        ORDER BY n.time DESC
    """)
    fun getNotificationsFromImportantContactsByCategory(category: String): LiveData<List<NotifInfo>>

    @Query("""
        SELECT n.* FROM notif_data n 
        INNER JOIN important_contacts c ON n.heading = c.contactName AND n.packageName = c.packageName
        ORDER BY n.time DESC
    """)
    fun getNotificationsFromImportantContacts(): LiveData<List<NotifInfo>>

    @Query("SELECT DISTINCT packageName FROM notif_data ORDER BY packageName ASC")
    fun getUniquePackageNamesSorted(): LiveData<List<String>>

    @Query("""
        SELECT n.* FROM notif_data n 
        INNER JOIN important_contacts c ON n.heading = c.contactName AND n.packageName = c.packageName
        WHERE c.category = :category AND n.time > :since
        ORDER BY n.time DESC
    """)
    suspend fun getNewImportantNotifications(category: String, since: Long): List<NotifInfo>

}

data class AppUsageStats(
    val packageName: String,
    val count: Int
)