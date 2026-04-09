package com.chaddy50.pomodoro.ui.screens.focusScreen.timer

import com.chaddy50.pomodoro.ui.screens.focusScreen.TimerType
import java.util.concurrent.TimeUnit

internal fun getTimerDisplayText(uiState: TimerUiState): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(uiState.timeLeftInMilliseconds)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(uiState.timeLeftInMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}

internal fun getNextBreakLabel(uiState: TimerUiState): String {
    if (!uiState.isTimerActive || uiState.timerType != TimerType.FocusUntil) return ""
    return when (uiState.nextTimerType) {
        TimerType.ShortBreak -> "Up next: Short break"
        TimerType.LongBreak -> "Up next: Long break"
        else -> ""
    }
}
