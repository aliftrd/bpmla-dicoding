package com.dicoding.asclepius.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.source.local.entity.History

@Database(entities = [History::class], version = 1)
abstract class AsclepiusDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AsclepiusDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AsclepiusDatabase {
            if (INSTANCE == null) {
                synchronized(AsclepiusDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AsclepiusDatabase::class.java, "asclepius_db"
                    )
                        .build()
                }
            }
            return INSTANCE as AsclepiusDatabase
        }
    }
}