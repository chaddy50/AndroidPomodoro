package com.chaddyt50.pomodoro.view

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.chaddyt50.pomodoro.MainActivity
import com.chaddyt50.pomodoro.R
import com.chaddyt50.pomodoro.component.TimerDisplay
import com.chaddyt50.pomodoro.viewmodel.PomodoroViewModel


@Composable
fun Pomodoro(
    context: Context,
    viewModel: PomodoroViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerDisplay(
            context,
            viewModel.focusUntilTimeInMilliseconds.value,
            viewModel.focusTimerTimeLeftInMilliseconds.value,
            viewModel.isFocusTimerActive.value,
            { viewModel.startFocusTimer() },
            "Focus until:",
            "Focus"
        )
    }

    if (viewModel.isFocusTimerFinished.value) {
        sendFocusTimerFinishedNotification(context)
    }
}

fun sendFocusTimerFinishedNotification(context: Context) {
    val activityIntent = Intent(context, MainActivity::class.java)

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