package com.mihir.notificationtracker.helper

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast

private lateinit var toast: Toast


@Suppress("DEPRECATION")
fun Context.getPackageInfo(packageName: String): PackageInfo {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        packageManager.getPackageInfo(packageName, 0)
    }
}

/**
 * Function used to show toast message: [message] Toast.LENGTH_LONG
 */
fun Context.showToastMessage(message: String) {
    if (::toast.isInitialized) {
        toast.cancel()
    }
    toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.show()
}

/**
 * Function used to Log.i [message] with tag as [tag] which has a default value of MIHIR_TAG_NOTIF
 */
fun Any.logThis(message: String, tag: String = "MIHIR_TAG_NOTIF") {
    Log.i(tag, message)
}