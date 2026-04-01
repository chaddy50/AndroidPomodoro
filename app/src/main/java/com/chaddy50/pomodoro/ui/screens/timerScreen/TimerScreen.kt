package com.chaddy50.pomodoro.ui.screens.timerScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    TimerDisplay(
        uiState,
        viewModel::startTimer
    )
}