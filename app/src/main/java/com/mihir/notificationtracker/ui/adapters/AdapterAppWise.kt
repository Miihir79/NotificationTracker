package com.mihir.notificationtracker.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mihir.notificationtracker.databinding.ItemAppNotifGrpBinding
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.helper.getDisplayNameFromPackageName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterAppWise(val onItemClick: ((packageName: String) -> Unit)) :
    ListAdapter<String, AdapterAppWise.ViewHolder>(ItemCallback) {

    private lateinit var context: Context
    private val dao = AppObjectController.appDatabase.notifDao()

    object ItemCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(private val binding: ItemAppNotifGrpBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) = binding.apply {
            binding.packageName = item
            binding.root.setOnClickListener {
                onItemClick(item)
            }
            
            // Check if any contact in this app is important
            val context = binding.root.context
            val badge = binding.root.findViewById<View>(context.resources.getIdentifier("ivImportantBadge", "id", context.packageName))
            if (badge != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val importantContacts = dao.getAllImportantContactsSync()
                    val hasImportant = importantContacts.any { it.packageName == item }
                    withContext(Dispatchers.Main) {
                        badge.visibility = if (hasImportant) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(ItemAppNotifGrpBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    var filter: CharSequence = ""
        set(value) {
            field = value
            onListOrFilterChange()
        }

    var packageNameData: List<String> = emptyList()
        set(value) {
            field = value
            onListOrFilterChange()
        }

    private fun onListOrFilterChange() {
        if (filter.length < 2) {
            submitList(packageNameData)
            return
        }
        val pattern = filter.toString().lowercase().trim()
        val filteredList = packageNameData.filter { pattern in it.getDisplayNameFromPackageName(context).lowercase() }
        submitList(filteredList)
    }

}