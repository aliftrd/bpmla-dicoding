package com.dicoding.asclepius.view.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.HistoryRepository

class HistoryViewModel(application: Application): ViewModel()  {
    private val historyRepository = HistoryRepository(application)

    fun getHistory() = historyRepository.get()
}