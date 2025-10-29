package com.chaddy50.pomodoro.screen

import android.content.Context
import androidx.compose.runtime.Composable
import com.chaddy50.pomodoro.notification.NotificationHandler
import com.chaddy50.pomodoro.viewmodel.PomodoroViewModel

interface Screen {
    val route: String

    @Composable
    fun Content(
        viewModel: PomodoroViewModel,
        context: Context,
        notificationHandler: NotificationHandler
    )
}