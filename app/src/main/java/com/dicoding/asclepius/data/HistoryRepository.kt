package com.dicoding.asclepius.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.source.local.entity.History
import com.dicoding.asclepius.data.source.local.room.AsclepiusDatabase
import com.dicoding.asclepius.data.source.local.room.HistoryDao
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {
    private val historyDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AsclepiusDatabase.getDatabase(application)
        historyDao = db.historyDao()
    }

    fun get(): LiveData<List<History>> = historyDao.get()

    fun create(history: History) {
        executorService.execute { historyDao.insert(history) }
    }
}