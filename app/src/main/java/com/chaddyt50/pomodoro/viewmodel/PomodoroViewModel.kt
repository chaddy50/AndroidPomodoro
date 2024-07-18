package com.chaddyt50.pomodoro.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.chaddyt50.pomodoro.model.TimerModel
import java.util.Calendar
import java.util.concurrent.TimeUnit

val MINIMUM_FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)
val HALF_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1) / 2

enum class DisplayMode {
    Focus,
    Break,
}

class PomodoroViewModel : ViewModel(), LifecycleEventObserver {
    //#region Properties
    private val _displayMode = mutableStateOf(DisplayMode.Focus)
    val displayMode: State<DisplayMode> = _displayMode
    //#endregion

    //#region Focus Timer
    private val _focusTimer = mutableStateOf(
        TimerModel(
            getFocusTimerLengthInMilliseconds(),
            ::onFocusTimerFinished
        )
    )
    val focusTimer: State<TimerModel> = _focusTimer

    fun startFocusTimer() {
        refreshFocusUntilTime()
        _focusTimer.value.start()
    }

    private fun refreshFocusUntilTime() {
        _focusTimer.value.updateTimeLeft(getFocusTimerLengthInMilliseconds())
    }

    private fun getFocusTimerLengthInMilliseconds(): Long {
        return getFocusUntilTimeInMilliseconds() - Calendar.getInstance().timeInMillis
    }

    private fun onFocusTimerFinished() {
        refreshFocusUntilTime()
    }

    private fun getFocusUntilTimeInMilliseconds(): Long {
        val currentTimeInMilliseconds = Calendar.getInstance().timeInMillis
        //return currentTimeInMilliseconds + TimeUnit.SECONDS.toMillis(2)

        val nextBreakLengthInMilliseconds = getNextBreakLengthInMilliseconds()

        val millisecondsSinceLastHalfHour = currentTimeInMilliseconds % HALF_HOUR_IN_MILLISECONDS
        val millisecondsUntilNextHalfHour =
            HALF_HOUR_IN_MILLISECONDS - millisecondsSinceLastHalfHour - nextBreakLengthInMilliseconds

        if (millisecondsUntilNextHalfHour < MINIMUM_FOCUS_TIME_IN_MILLISECONDS) {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour + HALF_HOUR_IN_MILLISECONDS
        } else {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour
        }
    }
    //#endregion

    //#region Break Timer
    private val _breakTimer = mutableStateOf(
        TimerModel(
            getNextBreakLengthInMilliseconds(),
            {}
        )
    )
    val breakTimer: State<TimerModel> = _breakTimer

    private fun getNextBreakLengthInMilliseconds(): Long {
        return TimeUnit.MINUTES.toMillis(5)
    }
    //#endregion

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == Lifecycle.Event.ON_RESUME) {
            refreshFocusUntilTime()
        }
    }

}