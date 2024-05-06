package com.dicoding.asclepius.view.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.source.local.entity.History
import com.dicoding.asclepius.databinding.HistoryItemBinding
import com.dicoding.asclepius.utils.Number
import com.dicoding.asclepius.utils.callback.HistoryDiffCallback

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val histories = ArrayList<History>()

    fun setHistories(histories: List<History>) {
        val diffCallback = HistoryDiffCallback(this.histories, histories)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.histories.clear()
        this.histories.addAll(histories)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(histories[position])
    }

    override fun getItemCount(): Int = histories.size

    inner class HistoryViewHolder(private val binding: HistoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            with(binding) {
                val bitmap = BitmapFactory.decodeByteArray(history.image, 0, history.image.size)
                historyImage.setImageBitmap(bitmap)
                "Category: ${history.category}".also { historyCategory.text = it }
                "Score: ${Number.decimalToPercentage(history.score)}".also { historyScore.text = it }
            }
        }
    }
}