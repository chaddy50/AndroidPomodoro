package com.chaddy50.pomodoro.ui.screens.focusScreen.timer

import com.chaddy50.pomodoro.ui.screens.focusScreen.TimerType

data class TimerUiState(
    val isTimerActive: Boolean = false,
    val timerType: TimerType = TimerType.FocusUntil,
    val timeLeftInMilliseconds: Long = 0L,
    val timerLengthInMilliseconds: Long = 0L,
    val focusUntilTimeInMilliseconds: Long = 0L,
    val sessionEndTimeInMilliseconds: Long = 0L,
    val sessionSegmentCount: Int = 0,
    val currentSegmentIndex: Int = 0,
    val nextTimerType: TimerType? = null,
)