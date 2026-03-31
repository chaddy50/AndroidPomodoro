package com.chaddy50.pomodoro.viewmodel

import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

val COUNT_DOWN_INTERVAL_SECONDS = TimeUnit.SECONDS.toMillis(1)
val HALF_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1) / 2
val MINIMUM_FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(15)
val SHORT_BREAK_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(5)
val LONG_BREAK_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)

class PomodoroViewModel : ViewModel(), LifecycleEventObserver {
    //#region Properties
    private val _timers = MutableStateFlow(listOf<PomodoroTimer>())
    val timers = _timers.asStateFlow()

    private val _activeTimerID = MutableStateFlow(0)
    val activeTimerID = _activeTimerID.asStateFlow()

    private val _focusTimerFinishedEvent = MutableSharedFlow<Unit>()
    val focusTimerFinishedEvent = _focusTimerFinishedEvent

    private val _breakTimerFinishedEvent = MutableSharedFlow<Unit>()
    val breakTimerFinishedEvent = _breakTimerFinishedEvent

    private var _countDownTimer: CountDownTimer? = null

    private var _maximumTimerID: Int = 0
    //#endregion

    init {
        addNextTimerAndBreak()
        activateNextTimer()
    }

    //#region Public Functions
    fun startTimer(timerID: Int?) {
        val timer = _timers.value.find { it.id == timerID } ?: return

        _countDownTimer?.cancel()
        _activeTimerID.value = timerID ?: -1

        val updatedTimer = timer.copy(isActive = true)
        _timers.value = _timers.value.map { if (it.id == timer.id) updatedTimer else it}

        _countDownTimer = createTimer(updatedTimer).also { it.start() }
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    fun addNextTimerAndBreak() {
        var focusTimerLengthInMilliseconds = getFocusTimerLengthInMilliseconds()

        val breakTimerType = when {
            focusTimerLengthInMilliseconds <= TimeUnit.MINUTES.toMillis(40) -> TimerType.ShortBreak
            else -> TimerType.LongBreak
        }

        focusTimerLengthInMilliseconds -= getBreakTimerLengthInMilliseconds(breakTimerType)

        val focusTimer = PomodoroTimer.create(++_maximumTimerID, TimerType.FocusUntil, focusTimerLengthInMilliseconds)
        val breakTimer = PomodoroTimer.create(++_maximumTimerID, breakTimerType, getBreakTimerLengthInMilliseconds(breakTimerType))
        _timers.value = _timers.value + focusTimer + breakTimer
    }

    fun activateNextTimer() {
        if (_timers.value.size >= activeTimerID.value) {
            _activeTimerID.value = _activeTimerID.value + 1
        }
    }
    //#endregion

    //#region Private Functions
    private fun createTimer(timer: PomodoroTimer): CountDownTimer {
        return object : CountDownTimer(
            timer.timeLeftInMilliseconds,
            COUNT_DOWN_INTERVAL_SECONDS
        ) {
            override fun onTick(millisecondsUntilFinished: Long) {
                val updatedTimer = timer.copy(timeLeftInMilliseconds = millisecondsUntilFinished)
                _timers.value = _timers.value.map { if (it.id == timer.id) updatedTimer else it}
            }

            override fun onFinish() {
                timer.timeLeftInMilliseconds = 0
                _timers.value = _timers.value.toList()

                when (timer.type) {
                    TimerType.FocusUntil -> {
                        activateNextTimer()
                        viewModelScope.launch {
                            _focusTimerFinishedEvent.emit(Unit)
                        }
                    }
                    else -> {
                        addNextTimerAndBreak()
                        activateNextTimer()
                        viewModelScope.launch {
                            _breakTimerFinishedEvent.emit(Unit)
                        }
                    }
                }
            }
        }
    }

    private fun getBreakTimerLengthInMilliseconds(breakTimerType: TimerType): Long {
        return when (breakTimerType) {
            TimerType.LongBreak -> LONG_BREAK_TIME_IN_MILLISECONDS
            TimerType.ShortBreak -> SHORT_BREAK_TIME_IN_MILLISECONDS
            else -> 0
        }
    }

    private fun getFocusTimerLengthInMilliseconds(): Long {
        val currentTimeInMilliseconds = Calendar.getInstance().timeInMillis
        return getFocusUntilTimeInMilliseconds() - currentTimeInMilliseconds
    }

    private fun getFocusUntilTimeInMilliseconds(
        currentTimeInMilliseconds: Long = Calendar.getInstance().timeInMillis,
    ): Long {
        val millisecondsSinceLastHalfHour = currentTimeInMilliseconds % HALF_HOUR_IN_MILLISECONDS
        val millisecondsUntilNextHalfHour = HALF_HOUR_IN_MILLISECONDS - millisecondsSinceLastHalfHour

        if (millisecondsUntilNextHalfHour < MINIMUM_FOCUS_TIME_IN_MILLISECONDS) {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour + HALF_HOUR_IN_MILLISECONDS
        } else {
            return currentTimeInMilliseconds + millisecondsUntilNextHalfHour
        }
    }
    //#endregion

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {}
}