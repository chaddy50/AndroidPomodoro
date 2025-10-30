package com.chaddy50.pomodoro.view.TimerScreen

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.chaddy50.pomodoro.notification.NotificationHandler
import com.chaddy50.pomodoro.view.Screen
import com.chaddy50.pomodoro.viewmodel.PomodoroViewModel
import com.chaddy50.pomodoro.viewmodel.TimerType

object TimerScreen : Screen {
    override val route = "pomodoro_timer"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    override fun Content(
        viewModel: PomodoroViewModel,
        context: Context,
        notificationHandler: NotificationHandler
    ) {
        val timers by viewModel.timers.collectAsState()
        val activeTimerID by viewModel.activeTimerID.collectAsState()
        val activeTimer = timers.find { it.id == activeTimerID}

        val timerType = activeTimer?.type
        val isTimerActive = activeTimer?.isActive
        val timerLengthInMilliseconds = activeTimer?.lengthInMilliseconds
        val timeLeftInMilliseconds = activeTimer?.timeLeftInMilliseconds
        val focusUntilTimeInMilliseconds = activeTimer?.focusUntilTimeInMilliseconds

        LaunchedEffect(Unit) {
            viewModel.timerFinishedEvent.collect {
                notificationHandler.sendFocusTimerFinishedNotification()
            }
        }

        TimerDisplay(
            context,
            isTimerActive ?: false,
            timeLeftInMilliseconds ?: 0,
            timerLengthInMilliseconds ?: 0,
            timerType ?: TimerType.FocusUntil,
            { viewModel.startTimer(activeTimerID) },
            focusUntilTimeInMilliseconds ?: 0
        )
    }
}