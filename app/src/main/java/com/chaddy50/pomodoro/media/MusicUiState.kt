package com.chaddy50.pomodoro.media

data class MusicUiState(
    val hasPermission: Boolean = false,
    val hasActiveSession: Boolean = false,
    val isPlaying: Boolean = false,
    val trackTitle: String = "",
    val artistName: String = "",
)
