package com.mihir.notificationtracker.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mihir.notificationtracker.databinding.FragmentImportantMessagesBinding
import com.mihir.notificationtracker.model.ImportantContact
import com.mihir.notificationtracker.ui.adapters.Adapter
import com.mihir.notificationtracker.ui.vm.ViewModel
import com.google.android.material.tabs.TabLayout

class ImportantMessagesFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private var _binding: FragmentImportantMessagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImportantMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = Adapter()
        adapter.showBadge = false
        binding.rvImportantNotifs.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                observeNotifications(adapter)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        observeNotifications(adapter)
    }

    private fun observeNotifications(adapter: Adapter) {
        val category = if (binding.tabLayout.selectedTabPosition == 0) 
            ImportantContact.CATEGORY_BUSINESS else ImportantContact.CATEGORY_PERSONAL

        viewModel.getImportantNotificationsByCategory(category).observe(viewLifecycleOwner) { notifs ->
            if (notifs.isNullOrEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.rvImportantNotifs.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.rvImportantNotifs.visibility = View.VISIBLE
                adapter.setData(ArrayList(notifs))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
