package com.chaddy50.pomodoro.ui.screens.focusScreen

import java.util.Calendar

enum class TimerType {
    FocusUntil,
    ShortBreak,
    LongBreak,
}

data class PomodoroTimer(
    val id: Int,
    val type: TimerType,
    val isActive: Boolean,
    val lengthInMilliseconds: Long,
    val timeLeftInMilliseconds: Long,
    val focusUntilTimeInMilliseconds: Long,
) {
    companion object {
        fun create(
            id: Int,
            type: TimerType,
            lengthInMilliseconds: Long,
            focusUntilEpochMilliseconds: Long = Calendar.getInstance().timeInMillis + lengthInMilliseconds,
        ): PomodoroTimer {
            return PomodoroTimer(
                id,
                type,
                false,
                lengthInMilliseconds,
                lengthInMilliseconds,
                focusUntilEpochMilliseconds
            )
        }
    }
}
