package com.chaddy50.pomodoro.ui.screens.homeScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerViewModel

@Composable
fun HomeScreen(viewModel: TimerViewModel, onStartFocus: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isTimerActive) {
        ActiveSessionDisplay(uiState = uiState, onReturnToFocus = onStartFocus)
    } else {
        IdleSessionDisplay(
            uiState = uiState,
            onStartFocus = onStartFocus,
            onSetSessionEndTime = viewModel::setSessionEndTime,
        )
    }
}
