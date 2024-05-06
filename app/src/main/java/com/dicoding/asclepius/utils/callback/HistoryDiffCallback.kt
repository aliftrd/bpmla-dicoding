package com.dicoding.asclepius.utils.callback

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.asclepius.data.source.local.entity.History

class HistoryDiffCallback(private val oldHistoryList: List<History>, private val newHistoryList: List<History>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldHistoryList.size
    override fun getNewListSize(): Int = newHistoryList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldHistoryList[oldItemPosition].id == newHistoryList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldHistory = oldHistoryList[oldItemPosition]
        val newHistory = newHistoryList[newItemPosition]
        return oldHistory.id == newHistory.id && oldHistory.category == newHistory.category
    }
}