package com.mihir.notificationtracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mihir.notificationtracker.databinding.ItemAppNotifGrpBinding

class AdapterAppWise(val onItemClick: ((packageName: String) -> Unit)) : RecyclerView.Adapter<AdapterAppWise.ViewHolder>() {

    private var packageNameArrayList = ArrayList<String>()

    inner class ViewHolder(private val binding: ItemAppNotifGrpBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) = binding.apply {
            binding.packageName = item
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAppNotifGrpBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(packageNameArrayList[position])
    }

    override fun getItemCount() = packageNameArrayList.size

    fun setData(data: ArrayList<String>) {
        packageNameArrayList = data
        notifyDataSetChanged()
    }
}