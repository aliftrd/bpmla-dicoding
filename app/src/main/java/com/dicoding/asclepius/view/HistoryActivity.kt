package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.source.local.entity.History
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.view.adapter.HistoryAdapter
import com.dicoding.asclepius.view.viewmodel.HistoryViewModel
import com.dicoding.asclepius.view.viewmodel.ViewModelFactory

class HistoryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val historyViewModel = obtainViewModel(this)
        historyViewModel.getHistory().observe(this) { histories ->
            if (histories.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            }
            setHistoryData(histories)
        }
    }

    private fun setHistoryData(history: List<History>) {
        binding.historyList.layoutManager = LinearLayoutManager(this)

        val historyAdapter = HistoryAdapter()
        historyAdapter.setHistories(history)
        binding.historyList.adapter = historyAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[HistoryViewModel::class.java]
    }
}