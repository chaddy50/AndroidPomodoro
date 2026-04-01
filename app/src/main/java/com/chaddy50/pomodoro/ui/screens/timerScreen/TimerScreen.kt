package com.chaddy50.pomodoro.ui.screens.timerScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaddy50.pomodoro.notification.NotificationHandler
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TimerScreen(
    notificationHandler: NotificationHandler,
    viewModel: TimerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        launch { viewModel.focusTimerFinishedEvent.collect { notificationHandler.sendFocusTimerFinishedNotification() } }
        launch { viewModel.breakTimerFinishedEvent.collect { notificationHandler.sendBreakTimerFinishedNotification() } }
    }

    TimerDisplay(
        context,
        uiState,
        viewModel::startTimer
    )
}