package com.chaddyt50.pomodoro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.chaddyt50.pomodoro.model.Timer
import java.util.Calendar
import java.util.concurrent.TimeUnit

val MINIMUM_FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)
val HALF_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1) / 2

class PomodoroViewModel : ViewModel(), LifecycleEventObserver {
    private val _focusUntilTimeInMilliseconds =
        mutableLongStateOf(getFocusUntilTimeInMilliseconds())
    val focusUntilTimeInMilliseconds: State<Long> = _focusUntilTimeInMilliseconds

    private val _focusTimer = mutableStateOf(
        Timer(
            getFocusTimerLengthInMilliseconds(),
            ::onFocusTimerFinished
        )
    )

    val isFocusTimerActive: State<Boolean> = _focusTimer.value.isActive
    val isFocusTimerFinished: State<Boolean> = _focusTimer.value.isFinished
    val focusTimerTimeLeftInMilliseconds: State<Long> = _focusTimer.value.timeLeftInMilliseconds

    fun startFocusTimer() {
        refreshFocusUntilTime()
        _focusTimer.value.start()
    }

    private fun refreshFocusUntilTime() {
        _focusUntilTimeInMilliseconds.longValue = getFocusUntilTimeInMilliseconds()

        _focusTimer.value.updateTimeLeft(getFocusTimerLengthInMilliseconds())
    }

    private fun getFocusTimerLengthInMilliseconds(): Long {
        return _focusUntilTimeInMilliseconds.longValue - Calendar.getInstance().timeInMillis
    }

    private fun onFocusTimerFinished() {
        refreshFocusUntilTime()
    }

    private fun getFocusUntilTimeInMilliseconds(): Long {
        val currentTimeInMilliseconds = Calendar.getInstance().timeInMillis
        //return currentTimeInMilliseconds + TimeUnit.SECONDS.toMillis(2)

        val nextBreakLengthInMilliseconds = TimeUnit.MINUTES.toMillis(5)

        val millisecondsSinceLastHalfHour = currentTimeInMilliseconds % HALF_HOUR_IN_MILLISECONDS
        val millisecondsUntilNextHalfHour =
            HALF_HOUR_IN_MILLISECONDS - millisecondsSinceLastHalfHour - nextBreakLengthInMilliseconds

        if (millisecondsUntilNextHalfHour < MINIMUM_FOCUS_TIME_IN_MILLISECONDS) {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour + HALF_HOUR_IN_MILLISECONDS
        } else {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == Lifecycle.Event.ON_RESUME) {
            refreshFocusUntilTime()
        }
    }

}