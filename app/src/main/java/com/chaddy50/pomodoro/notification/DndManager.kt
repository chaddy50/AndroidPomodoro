package com.chaddy50.pomodoro.notification

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DndManager(context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun hasPermission(): Boolean = notificationManager.isNotificationPolicyAccessGranted

    fun requestPermission(activity: Activity) {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Do Not Disturb Access Needed")
            .setMessage("This app needs Do Not Disturb access to silence your phone during focus sessions.")
            .setPositiveButton("Open Settings") { _, _ ->
                activity.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun enable() {
        if (hasPermission()) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        }
    }

    fun disable() {
        if (hasPermission()) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }
}
