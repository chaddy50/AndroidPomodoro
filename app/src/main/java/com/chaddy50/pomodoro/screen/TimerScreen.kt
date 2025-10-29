package com.chaddy50.pomodoro.screen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.chaddy50.pomodoro.notification.NotificationHandler
import com.chaddy50.pomodoro.view.composable.TimerDisplay
import com.chaddy50.pomodoro.viewmodel.PomodoroViewModel

object TimerScreen : Screen {
    override val route = "pomodoro_timer"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    override fun Content(
        viewModel: PomodoroViewModel,
        context: Context,
        notificationHandler: NotificationHandler
    ) {
        val isTimerFinished = viewModel.isFinished.collectAsState()
        val isTimerActive = viewModel.isTimerActive.collectAsState()
        val timerMode = viewModel.timerMode.collectAsState()
        val timerLengthInMilliseconds = viewModel.timerLengthInMilliseconds.collectAsState()
        val timeLeftInMilliseconds = viewModel.timeLeftInMilliseconds.collectAsState()
        val focusUntilTimeInMilliseconds = viewModel.focusUntilTimeInMilliseconds.collectAsState()

        TimerDisplay(
            context,
            isTimerActive.value,
            timeLeftInMilliseconds.value,
            timerLengthInMilliseconds.value,
            timerMode.value,
            viewModel::startTimer,
            focusUntilTimeInMilliseconds.value
        )

        if (isTimerFinished.value && notificationHandler.hasPermission()) {
            notificationHandler.sendFocusTimerFinishedNotification()
        }
    }
}

