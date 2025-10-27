package com.chaddy50.pomodoro.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.concurrent.TimeUnit

val COUNT_DOWN_INTERVAL_SECONDS = TimeUnit.SECONDS.toMillis(1)
val MINIMUM_FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)
val HALF_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1) / 2

enum class TimerMode {
    FocusUntil,
    Break
}

class PomodoroViewModel : ViewModel(), LifecycleEventObserver {
    //#region Properties
    private val _timerLengthInMilliseconds = MutableStateFlow<Long>(getFocusTimerLengthInMilliseconds())
    val timerLengthInMilliseconds = _timerLengthInMilliseconds.asStateFlow()

    private val _timeLeftInMilliseconds = MutableStateFlow<Long>(getFocusTimerLengthInMilliseconds())
    val timeLeftInMilliseconds = _timeLeftInMilliseconds.asStateFlow()

    private val _isTimerActive = MutableStateFlow(false)
    val isTimerActive = _isTimerActive.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private val _timerMode = MutableStateFlow(TimerMode.FocusUntil)
    val timerMode = _timerMode.asStateFlow()
    //#endregion

    private var _timer = createTimer()

    //#region Public Functions
    fun startTimer() {
        if (!_isTimerActive.value) {
            _isTimerActive.value = true
            _isFinished.value = false
            _timer.start()
        }
    }
    //#endregion

    //#region Private Functions
    private fun refreshFocusUntilTime() {
        when (_timerMode.value) {
            TimerMode.FocusUntil -> updateTimeLeft(getFocusTimerLengthInMilliseconds())
            else -> {}
        }
    }

    private fun updateTimeLeft(newTimeLeftInMilliseconds: Long) {
        _timerLengthInMilliseconds.value = newTimeLeftInMilliseconds
        _timeLeftInMilliseconds.value = newTimeLeftInMilliseconds
        _timer = createTimer()
    }

    private fun getFocusTimerLengthInMilliseconds(): Long {
        val currentTimeInMilliseconds = Calendar.getInstance().timeInMillis
        return getFocusUntilTimeInMilliseconds(currentTimeInMilliseconds) - currentTimeInMilliseconds
    }

    fun getFocusUntilTimeInMilliseconds(currentTimeInMilliseconds: Long): Long {
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

    private fun onFocusTimerFinished() {
        refreshFocusUntilTime()

        when (_timerMode.value) {
            TimerMode.FocusUntil -> {
                _timerMode.value = TimerMode.Break
                updateTimeLeft(getNextBreakLengthInMilliseconds())
            }
            TimerMode.Break ->
                _timerMode.value = TimerMode.FocusUntil
        }
    }

    private fun getNextBreakLengthInMilliseconds(): Long {
        return TimeUnit.MINUTES.toMillis(5)
    }

    private fun createTimer(): CountDownTimer {
        return object : CountDownTimer(
            _timeLeftInMilliseconds.value,
            COUNT_DOWN_INTERVAL_SECONDS
        ) {
            override fun onTick(millisecondsUntilFinished: Long) {
                _timeLeftInMilliseconds.value = millisecondsUntilFinished
            }

            override fun onFinish() {
                _isTimerActive.value = false
                _isFinished.value = true
                onFocusTimerFinished()
            }
        }
    }
    //#endregion

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == Lifecycle.Event.ON_RESUME) {
            if (!isTimerActive.value) {
                refreshFocusUntilTime()
            }
        }
    }

}