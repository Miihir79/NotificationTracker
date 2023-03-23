package com.mihir.notificationtracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mihir.notificationtracker.databinding.ItemNotifInfoBinding
import com.mihir.notificationtracker.model.NotifInfo

class Adapter: RecyclerView.Adapter<Adapter.ViewHolder>() {

    private var notifInfoArrayList = ArrayList<NotifInfo>()

    inner class ViewHolder(private val binding: ItemNotifInfoBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotifInfo) = binding.apply {
            binding.notifInfoItem = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotifInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifInfoArrayList[position])
    }

    override fun getItemCount() = notifInfoArrayList.size

    fun setData(data:ArrayList<NotifInfo>){
        notifInfoArrayList = data
        notifyDataSetChanged()
    }
}