package com.chaddy50.pomodoro.ui.screens.timerScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.chaddy50.pomodoro.media.MusicViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel, musicViewModel: MusicViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val musicUiState by musicViewModel.uiState.collectAsState()

    TimerDisplay(
        uiState = uiState,
        musicUiState = musicUiState,
        onStartTimer = viewModel::startTimer,
        onStopTimer = viewModel::stopTimer,
        onPlayPause = musicViewModel::playPause,
        onNext = musicViewModel::next,
        onPrevious = musicViewModel::previous,
    )
}
