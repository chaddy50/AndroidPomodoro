package com.chaddy50.pomodoro.ui.screens.timerScreen

data class TimerUiState(
    val isTimerActive: Boolean = false,
    val timerType: TimerType = TimerType.FocusUntil,
    val timeLeftInMilliseconds: Long = 0L,
    val timerLengthInMilliseconds: Long = 0L,
    val focusUntilTimeInMilliseconds: Long = 0L,
)