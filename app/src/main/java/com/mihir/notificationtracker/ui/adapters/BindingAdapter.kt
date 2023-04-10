package com.mihir.notificationtracker.ui.adapters

import android.content.pm.PackageManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mihir.notificationtracker.R
import com.mihir.notificationtracker.helper.getDisplayNameFromPackageName
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
        this.setImageResource(R.drawable.ic_nav_app_notif)
        e.printStackTrace()
    }
}

@BindingAdapter("getAppNameFromPackageName")
fun TextView.getAppNameFromPackageName(packageName: String) {
    this.text = packageName.getDisplayNameFromPackageName(context)
}