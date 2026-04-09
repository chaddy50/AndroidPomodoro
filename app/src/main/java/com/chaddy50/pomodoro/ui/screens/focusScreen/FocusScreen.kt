package com.chaddy50.pomodoro.ui.screens.focusScreen

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls.MediaControlsViewModel
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerViewModel

@Composable
fun FocusScreen(
    viewModel: TimerViewModel,
    musicViewModel: MediaControlsViewModel,
    onStopFocus: () -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    DisposableEffect(Unit) {
        val original = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        onDispose { activity.requestedOrientation = original }
    }

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
