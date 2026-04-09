package com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls

data class MediaControlsUiState(
    val hasPermission: Boolean = false,
    val hasActiveSession: Boolean = false,
    val isPlaying: Boolean = false,
    val trackTitle: String = "",
    val artistName: String = "",
)