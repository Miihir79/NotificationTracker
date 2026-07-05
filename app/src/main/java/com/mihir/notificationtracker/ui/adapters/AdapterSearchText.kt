package com.mihir.notificationtracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mihir.notificationtracker.databinding.ItemNotifInfoBinding
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.helper.getDisplayNameFromPackageName
import com.mihir.notificationtracker.model.NotifInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterSearchText : ListAdapter<NotifInfo, AdapterSearchText.ViewHolder>(ItemCallback) {

    private val dao = AppObjectController.appDatabase.notifDao()

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
            val context = binding.root.context
            
            // Check if contact is important
            val badge = binding.root.findViewById<View>(context.resources.getIdentifier("ivImportantBadge", "id", context.packageName))
            if (badge != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val important = dao.getImportantContact(item.heading, item.packageName)
                    withContext(Dispatchers.Main) {
                        badge.visibility = if (important != null) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotifInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}