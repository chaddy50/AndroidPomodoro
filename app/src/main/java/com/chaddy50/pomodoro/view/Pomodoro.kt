package com.chaddy50.pomodoro.view

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chaddy50.pomodoro.composable.TimerDisplay
import com.chaddy50.pomodoro.notification.NotificationHandler
import com.chaddy50.pomodoro.viewmodel.PomodoroViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Pomodoro(
    context: Context,
    viewModel: PomodoroViewModel,
    notificationHandler: NotificationHandler
) {
    val isTimerFinished = viewModel.isFinished.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerDisplay(
            context,
            viewModel
        )
    }

    if (isTimerFinished.value && notificationHandler.hasPermission()) {
        notificationHandler.sendFocusTimerFinishedNotification()
    }
}

