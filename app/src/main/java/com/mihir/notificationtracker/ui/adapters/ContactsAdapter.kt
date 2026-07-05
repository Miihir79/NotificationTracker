package com.mihir.notificationtracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.ItemContactManageBinding
import com.mihir.notificationtracker.model.ImportantContact

class ContactsAdapter(
    private val onDeleteClick: (ImportantContact) -> Unit,
    private val onItemClick: (ImportantContact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private var importantContacts = listOf<ImportantContact>()

    fun setData(importantContacts: List<ImportantContact>) {
        this.importantContacts = importantContacts
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemContactManageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemContactManageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val importantContact = importantContacts[position]

        holder.binding.tvContactName.text = importantContact.contactName
        
        try {
            val icon = holder.itemView.context.packageManager.getApplicationIcon(importantContact.packageName)
            holder.binding.ivAppIcon.setImageDrawable(icon)
        } catch (e: Exception) {
            holder.binding.ivAppIcon.setImageResource(R.drawable.ic_nav_app_notif)
        }

        holder.binding.ivDelete.setOnClickListener {
            onDeleteClick(importantContact)
        }

        holder.binding.root.setOnClickListener {
            onItemClick(importantContact)
        }
    }

    override fun getItemCount() = importantContacts.size
}
