package com.mihir.notificationtracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notif_data")
data class NotifInfo(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val packageName: String,
    val heading: String,
    val bodyText: String,
    val time: Long
)
