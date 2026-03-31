package com.chaddy50.pomodoro.viewmodel

import java.util.Calendar

enum class TimerType {
    FocusUntil,
    ShortBreak,
    LongBreak,
}

data class PomodoroTimer(
    val id: Int,
    var type: TimerType,
    var isActive: Boolean,
    var lengthInMilliseconds: Long,
    var timeLeftInMilliseconds: Long,
    var focusUntilTimeInMilliseconds: Long,
) {
    companion object {
        fun create(
            id: Int,
            type: TimerType,
            lengthInMilliseconds: Long,
        ): PomodoroTimer {
            return PomodoroTimer(
                id,
                type,
                false,
                lengthInMilliseconds,
                lengthInMilliseconds,
                Calendar.getInstance().timeInMillis + lengthInMilliseconds
            )
        }
    }
}
