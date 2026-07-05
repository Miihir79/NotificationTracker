package com.mihir.notificationtracker.ui.screens

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mihir.notificationtracker.databinding.FragmentReminderSettingsBinding
import com.mihir.notificationtracker.helper.ReminderManager
import com.mihir.notificationtracker.model.ImportantContact
import com.mihir.notificationtracker.ui.vm.ViewModel
import java.util.*

class ReminderSettingsFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this)[ViewModel::class.java] }
    private var _binding: FragmentReminderSettingsBinding? = null
    private val binding get() = _binding!!

    private val prefs by lazy { requireContext().getSharedPreferences("notif_tracker_prefs", Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateTimeButtons()

        binding.btnBusinessTime.setOnClickListener {
            showTimePicker("business_time") {
                updateTimeButtons()
                scheduleReminder(ImportantContact.CATEGORY_BUSINESS)
            }
        }

        binding.btnPersonalTime.setOnClickListener {
            showTimePicker("personal_time") {
                updateTimeButtons()
                scheduleReminder(ImportantContact.CATEGORY_PERSONAL)
            }
        }

        binding.btnBusinessSound.setOnClickListener {
            openChannelSettings("business_important_channel")
        }

        binding.btnPersonalSound.setOnClickListener {
            openChannelSettings("personal_important_channel")
        }

        binding.btnClearLogs.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Logs")
                .setMessage("This will permanently delete all notification history. Important contacts will remain saved.")
                .setPositiveButton("Clear") { _, _ ->
                    viewModel.clearAllNotifications()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun openChannelSettings(channelId: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
            }
            startActivity(intent)
        } else {
            val intent = Intent().apply {
                action = "android.settings.APP_NOTIFICATION_SETTINGS"
                putExtra("app_package", requireContext().packageName)
                putExtra("app_uid", requireContext().applicationInfo.uid)
            }
            startActivity(intent)
        }
    }

    private fun updateTimeButtons() {
        binding.btnBusinessTime.text = prefs.getString("business_time", "20:00")
        binding.btnPersonalTime.text = prefs.getString("personal_time", "21:00")
    }

    private fun showTimePicker(key: String, onTimeSet: () -> Unit) {
        val currentTime = prefs.getString(key, "20:00") ?: "20:00"
        val parts = currentTime.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        TimePickerDialog(requireContext(), { _, h, m ->
            val time = String.format(Locale.getDefault(), "%02d:%02d", h, m)
            prefs.edit().putString(key, time).apply()
            onTimeSet()
        }, hour, minute, true).show()
    }

    private fun scheduleReminder(category: String) {
        ReminderManager.scheduleReminder(requireContext(), category)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
