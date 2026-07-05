package com.mihir.notificationtracker.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.DialogAddContactBinding
import com.mihir.notificationtracker.databinding.FragmentManageContactsBinding
import com.mihir.notificationtracker.helper.getDisplayNameFromPackageName
import com.mihir.notificationtracker.model.ImportantContact
import com.mihir.notificationtracker.ui.adapters.ContactsAdapter
import com.mihir.notificationtracker.ui.vm.ViewModel

class ManageContactsFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private var _binding: FragmentManageContactsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactsAdapter(
            onDeleteClick = { contact ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Important Contact")
                    .setMessage("Are you sure you want to remove ${contact.contactName} from important contacts?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.removeImportantContact(contact)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onItemClick = { contact ->
                val intent = Intent(requireContext(), AppNotificationActivity::class.java).apply {
                    putExtra("packageName", contact.packageName)
                    putExtra("contactName", contact.contactName)
                }
                startActivity(intent)
            }
        )
        binding.rvContacts.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                observeImportantContacts()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        observeImportantContacts()
        
        binding.fabAddContact.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun observeImportantContacts() {
        val category = if (binding.tabLayout.selectedTabPosition == 0) 
            ImportantContact.CATEGORY_BUSINESS else ImportantContact.CATEGORY_PERSONAL

        viewModel.getImportantContactsByCategory(category).observe(viewLifecycleOwner) { important ->
            adapter.setData(important)
        }
    }

    private fun showAddContactDialog() {
        val dialogBinding = DialogAddContactBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Important Contact")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val app = dialogBinding.atvAppSelector.tag as? String ?: ""
                val contact = dialogBinding.atvContactSelector.text.toString()
                val category = if (dialogBinding.chipBusiness.isChecked) 
                    ImportantContact.CATEGORY_BUSINESS else ImportantContact.CATEGORY_PERSONAL
                
                if (app.isNotEmpty() && contact.isNotEmpty()) {
                    viewModel.addImportantContact(contact, app, category)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        viewModel.uniquePackageNames.observe(viewLifecycleOwner) { packages ->
            val appDisplayList = packages.map { pkg ->
                AppDisplayInfo(pkg.getDisplayNameFromPackageName(requireContext()), pkg)
            }
            val appAdapter = object : ArrayAdapter<AppDisplayInfo>(
                requireContext(), R.layout.item_app_selector, appDisplayList
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_app_selector, parent, false)
                    val info = getItem(position)
                    val ivIcon = view.findViewById<ImageView>(R.id.ivAppIcon)
                    val tvName = view.findViewById<TextView>(R.id.tvAppName)
                    tvName.text = info?.appName
                    info?.packageName?.let { pkg ->
                        try {
                            ivIcon.setImageDrawable(requireContext().packageManager.getApplicationIcon(pkg))
                        } catch (e: Exception) {
                            ivIcon.setImageResource(R.drawable.ic_nav_app_notif)
                        }
                    }
                    return view
                }
            }
            dialogBinding.atvAppSelector.setAdapter(appAdapter)
        }

        dialogBinding.atvAppSelector.setOnItemClickListener { parent, _, position, _ ->
            val info = parent.getItemAtPosition(position) as AppDisplayInfo
            val pkg = info.packageName ?: ""
            dialogBinding.atvAppSelector.tag = pkg
            dialogBinding.atvAppSelector.setText(info.appName, false)

            viewModel.getUniqueContacts(pkg).observe(viewLifecycleOwner) { contacts ->
                val contactAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, contacts)
                dialogBinding.atvContactSelector.setAdapter(contactAdapter)
                dialogBinding.atvContactSelector.setText("", false)
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
