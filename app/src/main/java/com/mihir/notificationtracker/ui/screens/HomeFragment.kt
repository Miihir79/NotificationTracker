package com.mihir.notificationtracker.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.mihir.notificationtracker.databinding.FragmentHomeBinding
import com.mihir.notificationtracker.model.NotifInfo
import com.mihir.notificationtracker.ui.adapters.Adapter
import com.mihir.notificationtracker.ui.vm.ViewModel

class HomeFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private val adapter by lazy { Adapter() }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRecentNotifs.adapter = adapter
        observe()
    }

    private fun observe() {
        viewModel.readATopNotifications.observe(viewLifecycleOwner) {
            adapter.setData(it as ArrayList<NotifInfo>)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}