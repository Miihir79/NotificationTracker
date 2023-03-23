package com.mihir.notificationtracker.ui.screens

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.ActivityAppNotificationBinding
import com.mihir.notificationtracker.model.NotifInfo
import com.mihir.notificationtracker.ui.adapters.Adapter
import com.mihir.notificationtracker.ui.vm.AppWiseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppNotificationActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this)[AppWiseViewModel::class.java] }
    private val adapter by lazy { Adapter() }
    private val binding by lazy { ActivityAppNotificationBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Notifications of this app"
        setContentView(binding.root)
        val packageName = intent.getStringExtra("packageName")
        if (packageName != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getAppNotifs(packageName)
            }
        }
        binding.recycler.adapter = adapter
        observe()
    }

    private fun observe() {
        viewModel.observeAppData.observe(this) {
            adapter.setData(it as ArrayList<NotifInfo>)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}