package com.mihir.notificationtracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mihir.notificationtracker.databinding.ItemNotifInfoBinding
import com.mihir.notificationtracker.helper.getDisplayNameFromPackageName
import com.mihir.notificationtracker.model.NotifInfo

class AdapterSearchText : ListAdapter<NotifInfo, AdapterSearchText.ViewHolder>(ItemCallback) {

    object ItemCallback : DiffUtil.ItemCallback<NotifInfo>() {
        override fun areItemsTheSame(oldItem: NotifInfo, newItem: NotifInfo): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: NotifInfo, newItem: NotifInfo): Boolean =
            oldItem == newItem
    }

    var filter: CharSequence = ""
        set(value) {
            field = value
            onListOrFilterChange()
        }

    var notifInfoData: List<NotifInfo> = emptyList()
        set(value) {
            field = value
            onListOrFilterChange()
        }

    private fun onListOrFilterChange() {
        if (filter.length < 2) {
            submitList(notifInfoData)
            return
        }
        val pattern = filter.toString().lowercase().trim()
        val filteredList = notifInfoData.filter { pattern in it.bodyText.lowercase() || pattern in it.heading.lowercase() }
        submitList(filteredList)
    }

    inner class ViewHolder(private val binding: ItemNotifInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotifInfo) = binding.apply {
            binding.notifInfoItem = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotifInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}