package com.chaddy50.pomodoro.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.chaddy50.pomodoro.PomodoroApp
import com.chaddy50.pomodoro.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotificationHandler(
    private val context: Context,
    private val requestPermissionLauncher: ActivityResultLauncher<String>
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        return permission == PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermission(mainActivity: PomodoroApp) {
        when {
            ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.POST_NOTIFICATIONS) -> {
                MaterialAlertDialogBuilder(mainActivity)
                    .setTitle("Notification Permission Needed")
                    .setMessage("This app needs notification access to notify you when a focus period ends. Without it, you won’t get notifications when a period ends.")
                    .setPositiveButton("OK") { _, _ ->
                        // User understood — request permission again
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss() // Respect user’s decision
                    }
                    .show()
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun sendFocusTimerFinishedNotification() {
        val activityIntent = Intent(context, PomodoroApp::class.java)

        val activityPendingIntent = PendingIntent.getActivity(
            context,
            54321,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "Notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Focus time is over")
            .setContentText("Take a break!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityPendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(12345, builder.build())
    }
}