package com.mihir.notificationtracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.ItemNotifInfoBinding
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.model.NotifInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>() {

    private var notifInfoArrayList = ArrayList<NotifInfo>()
    private val dao = AppObjectController.appDatabase.notifDao()
    var showBadge: Boolean = true

    inner class ViewHolder(private val binding: ItemNotifInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotifInfo) = binding.apply {
            binding.notifInfoItem = item
            val context = binding.root.context
            
            // Check if contact is important
            val badge = binding.root.findViewById<View>(context.resources.getIdentifier("ivImportantBadge", "id", context.packageName))
            if (showBadge && badge != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val important = dao.getImportantContact(item.heading, item.packageName)
                    withContext(Dispatchers.Main) {
                        badge.visibility = if (important != null) View.VISIBLE else View.GONE
                    }
                }
            } else {
                badge?.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotifInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifInfoArrayList[position])
    }

    override fun getItemCount() = notifInfoArrayList.size

    fun setData(data: ArrayList<NotifInfo>) {
        notifInfoArrayList = data
        notifyDataSetChanged()
    }
}