package com.mihir.notificationtracker.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.mihir.notificationtracker.databinding.FragmentAppWiseNotificationBinding
import com.mihir.notificationtracker.helper.getDisplayNameFromPackageName
import com.mihir.notificationtracker.model.NotifInfo
import com.mihir.notificationtracker.ui.vm.ViewModel
import com.mihir.notificationtracker.ui.adapters.AdapterAppWise

class AppWiseNotificationFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private val adapter by lazy {
        AdapterAppWise {
            val intent = Intent(context, AppNotificationActivity::class.java)
            intent.putExtra("packageName", it)
            startActivity(intent)
        }
    }

    private var _binding: FragmentAppWiseNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppWiseNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAppWiseNotifs.adapter = adapter
        binding.searchApps.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String): Boolean {
                adapter.filter = p0
                if (p0 == "") {
                    binding.rvAppWiseNotifs.scrollToPosition(0)
                }
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }
        })

        observe()
    }

    private fun observe() {
        viewModel.getAppWiseNotifs.observe(viewLifecycleOwner) { data ->
            val myMap = mutableMapOf<String, ArrayList<NotifInfo>>()
            val listDisplay = data.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.packageName.getDisplayNameFromPackageName(requireContext()) }
            )
            listDisplay.forEach {
                if (myMap.containsKey(it.packageName)) {
                    myMap[it.packageName]?.add(it)
                } else {
                    val list = ArrayList<NotifInfo>()
                    list.add(it)
                    myMap[it.packageName] = list
                }
            }
            binding.progressBar.visibility = View.GONE
            if (myMap.keys.size > 1) {
                adapter.packageNameData = myMap.keys.toList()
            } else if (myMap.keys.size == 1) { // when only notif of one app is in db, it was map.keys throwing error thus handled separately
                val list = arrayListOf<String>()
                list.add(data[0].packageName)
                adapter.packageNameData = list
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}