package com.chaddyt50.pomodoro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.chaddyt50.pomodoro.model.Timer
import java.util.Calendar
import java.util.concurrent.TimeUnit

val MINIMUM_FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)
val HALF_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1) / 2

class FocusTimerViewModel : ViewModel() {
    private val _focusUntilTimeInMilliseconds =
        mutableLongStateOf(getFocusUntilTimeInMilliseconds())
    val focusUntilTimeInMilliseconds: State<Long> = _focusUntilTimeInMilliseconds

    private val _focusTimer = mutableStateOf(
        Timer(
            _focusUntilTimeInMilliseconds.longValue - Calendar.getInstance().timeInMillis,
            {})
    )
    val focusTimer: State<Timer> = _focusTimer

    private fun getFocusUntilTimeInMilliseconds(): Long {
        val nextBreakLengthInMilliseconds = TimeUnit.MINUTES.toMillis(5)

        val currentTimeInMilliseconds = Calendar.getInstance().timeInMillis
        val millisecondsSinceLastHalfHour = currentTimeInMilliseconds % HALF_HOUR_IN_MILLISECONDS
        val millisecondsUntilNextHalfHour =
            HALF_HOUR_IN_MILLISECONDS - millisecondsSinceLastHalfHour - nextBreakLengthInMilliseconds

        if (millisecondsUntilNextHalfHour < MINIMUM_FOCUS_TIME_IN_MILLISECONDS) {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour + HALF_HOUR_IN_MILLISECONDS
        } else {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour
        }
    }
}