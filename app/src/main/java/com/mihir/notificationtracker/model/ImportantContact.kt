package com.mihir.notificationtracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "important_contacts")
data class ImportantContact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val contactName: String,
    val packageName: String,
    val category: String // "BUSINESS" or "PERSONAL"
) {
    companion object {
        const val CATEGORY_BUSINESS = "BUSINESS"
        const val CATEGORY_PERSONAL = "PERSONAL"
    }
}
