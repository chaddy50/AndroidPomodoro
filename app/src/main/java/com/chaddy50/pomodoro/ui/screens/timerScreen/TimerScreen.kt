package com.chaddy50.pomodoro.ui.screens.timerScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    TimerDisplay(
        context,
        uiState,
        viewModel::startTimer
    )
}