package com.mihir.notificationtracker.ui.screens

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.text.TextUtils
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.mihir.notificationtracker.helper.AppObjectController
import com.mihir.notificationtracker.NotificationInterceptor
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.databinding.ActivityMainBinding
import com.mihir.notificationtracker.helper.ReminderManager
import com.mihir.notificationtracker.model.ImportantContact

class MainActivity : AppCompatActivity() {

    private val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestNotificationPermission()
        }

        if (isNotificationServiceEnabled().not()) {
            showNotificationListenerAlertDialog()
        }
        startService(Intent(AppObjectController.applicationContext, NotificationInterceptor::class.java))
        setupDrawerLayout()
        setAppVersion()
        
        // Ensure reminders are scheduled
        ReminderManager.scheduleReminder(this, ImportantContact.CATEGORY_BUSINESS)
        ReminderManager.scheduleReminder(this, ImportantContact.CATEGORY_PERSONAL)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "importantMessagesFragment") {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.importantMessagesFragment)
        }
    }

    private fun setAppVersion() {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            val version = pInfo.versionName
            binding.tvAppVersion.text = "Version $version"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showNotificationListenerAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.notification_listener_title)
            .setMessage(R.string.notification_listener_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setupDrawerLayout() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        // Fix: Manually close drawer when navigating, as the custom layout wrapper may interfere with auto-closing
        navController.addOnDestinationChangedListener { _, _, _ ->
            binding.drawerLayout.closeDrawers()
        }

        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat: String = Settings.Secure.getString(
            contentResolver,
            ENABLED_NOTIFICATION_LISTENERS
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

}