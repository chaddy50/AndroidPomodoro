package com.chaddy50.pomodoro.viewmodel

import java.util.Calendar
import java.util.concurrent.TimeUnit

val MINIMUM_FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)

enum class TimerType {
    FocusUntil,
    ShortBreak,
    LongBreak,
    None
}

data class PomodoroTimer(
    val id: Int,
    var type: TimerType,
    var isActive: Boolean,
    var lengthInMilliseconds: Long,
    var timeLeftInMilliseconds: Long,
    var focusUntilTimeInMilliseconds: Long,
    val nextTimerType: TimerType,
) {
    companion object {
        fun create(
            id: Int,
            type: TimerType,
            nextType: TimerType = TimerType.None
        ): PomodoroTimer {
            val length = when (type) {
                TimerType.FocusUntil -> getFocusTimerLengthInMilliseconds(nextType)
                TimerType.LongBreak -> getLongBreakLengthInMilliseconds()
                TimerType.ShortBreak -> getShortBreakLengthInMilliseconds()
                TimerType.None -> 0
            }
            return PomodoroTimer(
                id,
                type,
                false,
                length,
                length,
                getFocusUntilTimeInMilliseconds(nextType),
                nextType,
            )
        }

        private fun getFocusTimerLengthInMilliseconds(nextTimerType: TimerType): Long {
            val currentTimeInMilliseconds = Calendar.getInstance().timeInMillis
            return getFocusUntilTimeInMilliseconds(nextTimerType) - currentTimeInMilliseconds
        }

        private fun getFocusUntilTimeInMilliseconds(
            nextTimerType: TimerType,
            currentTimeInMilliseconds: Long = Calendar.getInstance().timeInMillis,
        ): Long {
            val nextBreakLengthInMilliseconds = getNextBreakLengthInMilliseconds(nextTimerType)

            val millisecondsSinceLastHalfHour = currentTimeInMilliseconds % HALF_HOUR_IN_MILLISECONDS
            val millisecondsUntilNextHalfHour =
                HALF_HOUR_IN_MILLISECONDS - millisecondsSinceLastHalfHour - nextBreakLengthInMilliseconds

            if (millisecondsUntilNextHalfHour < MINIMUM_FOCUS_TIME_IN_MILLISECONDS) {
                return currentTimeInMilliseconds + millisecondsUntilNextHalfHour + HALF_HOUR_IN_MILLISECONDS
            } else {
                return currentTimeInMilliseconds + millisecondsUntilNextHalfHour
            }
        }

        private fun getNextBreakLengthInMilliseconds(nextTimerType: TimerType): Long {
            return when (nextTimerType) {
                TimerType.ShortBreak -> getShortBreakLengthInMilliseconds()
                TimerType.LongBreak -> getLongBreakLengthInMilliseconds()
                else -> 0
            }
        }

        private fun getShortBreakLengthInMilliseconds(): Long {
            return TimeUnit.MINUTES.toMillis(5)
        }

        private fun getLongBreakLengthInMilliseconds(): Long {
            return TimeUnit.MINUTES.toMillis(15)
        }
    }
}
