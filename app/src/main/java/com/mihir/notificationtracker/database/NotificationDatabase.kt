package com.mihir.notificationtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mihir.notificationtracker.model.ImportantContact
import com.mihir.notificationtracker.model.NotifInfo

@Database(entities = [NotifInfo::class, ImportantContact::class], version = 2, exportSchema = false)
abstract class NotificationDatabase : RoomDatabase() {

    abstract fun notifDao(): NotificationInterface

    companion object {
        @Volatile
        private var INSTANCE: NotificationDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `important_contacts` (`id` INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL, `contactName` TEXT NOT NULL, `packageName` TEXT NOT NULL, `category` TEXT NOT NULL)")
            }
        }

        fun getDatabase(context: Context): NotificationDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotificationDatabase::class.java,
                    "notif_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
