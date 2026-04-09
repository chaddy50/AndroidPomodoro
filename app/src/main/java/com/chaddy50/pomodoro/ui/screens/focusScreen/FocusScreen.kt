package com.chaddy50.pomodoro.ui.screens.focusScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls.MediaControlsViewModel
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerViewModel

@Composable
fun FocusScreen(
    viewModel: TimerViewModel,
    musicViewModel: MediaControlsViewModel,
    onStopFocus: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val musicUiState by musicViewModel.uiState.collectAsState()

    FocusDisplay(
        timerUiState = uiState,
        musicUiState = musicUiState,
        onStartTimer = viewModel::startTimer,
        onStopTimer = {
            viewModel.stopTimer()
            onStopFocus()
        },
        onPlayPause = musicViewModel::playPause,
        onNext = musicViewModel::next,
        onPrevious = musicViewModel::previous,
    )
}
