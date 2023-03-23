package com.mihir.notificationtracker.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.FragmentAppWiseNotificationBinding
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
        observe()
    }

    private fun observe() {
        viewModel.getAppWiseNotifs.observe(viewLifecycleOwner) { data ->
            val myMap = mutableMapOf<String, ArrayList<NotifInfo>>()
            data.forEach {
                if (myMap.containsKey(it.packageName)) {
                    myMap[it.packageName]?.add(it)
                } else {
                    val list = ArrayList<NotifInfo>()
                    list.add(it)
                    myMap[it.packageName] = list
                }
            }
            // TODO: Handle click for each app and then open all of it's notification from myMap key
            if (myMap.keys.size >= 1) {
                adapter.setData(myMap.keys.toList() as ArrayList<String>)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}