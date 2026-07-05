package com.mihir.notificationtracker.ui.screens

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.ActivityAppNotificationBinding
import com.mihir.notificationtracker.ui.adapters.AdapterSearchText
import com.mihir.notificationtracker.ui.vm.AppWiseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AppNotificationActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this)[AppWiseViewModel::class.java] }
    private val adapter by lazy { AdapterSearchText() }
    private val binding by lazy { ActivityAppNotificationBinding.inflate(layoutInflater) }
    
    private var selectedStartTime: Long? = null
    private var selectedEndTime: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val packageName = intent.getStringExtra("packageName")
        val contactName = intent.getStringExtra("contactName")
        
        supportActionBar?.title = contactName ?: "Notifications of this app"
        
        if (packageName != null) {
            refreshData(packageName, contactName)
        }

        binding.searchInApp.setOnClickListener {
            binding.searchInApp.isIconified = false
        }
        
        binding.btnFilterDate.setOnClickListener {
            showDatePicker(packageName, contactName)
        }

        binding.searchInApp.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String): Boolean {
                adapter.filter = p0
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }
        })

        binding.recycler.adapter = adapter
        observe()
    }
    
    private fun refreshData(packageName: String, contactName: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAppNotifs(packageName, contactName, selectedStartTime, selectedEndTime)
            viewModel.getAppNotifCount(packageName)
            viewModel.getAppNotifCountToday(packageName)
        }
    }

    private fun showDatePicker(packageName: String?, contactName: String?) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            selectedStartTime = selectedCalendar.timeInMillis
            
            selectedCalendar.set(year, month, dayOfMonth, 23, 59, 59)
            selectedEndTime = selectedCalendar.timeInMillis
            
            if (packageName != null) {
                refreshData(packageName, contactName)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun observe() {
        viewModel.observeAppData.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.recycler.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.GONE
                binding.recycler.visibility = View.VISIBLE
                adapter.notifInfoData = it
            }
        }

        viewModel.todayNotificationCount.observe(this) {
            binding.chipToday.text = getString(R.string.today_s_notifs) + it.toString()
        }

        viewModel.totalNotificationCount.observe(this) {
            binding.chipTotal.text = getString(R.string.total_notifs) + it.toString()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}
