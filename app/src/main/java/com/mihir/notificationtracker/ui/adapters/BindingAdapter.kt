package com.mihir.notificationtracker.ui.adapters

import android.content.pm.PackageManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("displayTime")
fun TextView.displayTime(time: Long) {
    val dateString: String = SimpleDateFormat("HH:mm dd/MM/yy", Locale.ENGLISH).format(Date(time))
    this.text = dateString
}

@BindingAdapter("getImageFromPackageName")
fun ImageView.getImageFromPackageName(packageName: String) {
    try {
        val icon = context.packageManager.getApplicationIcon(packageName)
        this.setImageDrawable(icon)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
}

@BindingAdapter("getAppNameFromPackageName")
fun TextView.getAppNameFromPackageName(packageName: String) {
    try {
        val info = context.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val appName = context.packageManager.getApplicationLabel(info)
        this.text = appName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
}