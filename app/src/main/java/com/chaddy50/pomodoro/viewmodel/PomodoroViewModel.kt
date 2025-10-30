package com.chaddy50.pomodoro.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

val COUNT_DOWN_INTERVAL_SECONDS = TimeUnit.SECONDS.toMillis(1)
val HALF_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1) / 2

class PomodoroViewModel : ViewModel(), LifecycleEventObserver {
    //#region Properties
    private val _timers = MutableStateFlow(
        listOf(
            PomodoroTimer.create(1, TimerType.FocusUntil, TimerType.LongBreak),
            PomodoroTimer.create(2, TimerType.LongBreak),
        )
    )
    val timers = _timers.asStateFlow()

    private val _activeTimerID = MutableStateFlow(1)
    val activeTimerID = _activeTimerID.asStateFlow()

    private val _timerFinishedEvent = MutableSharedFlow<Unit>()
    val timerFinishedEvent = _timerFinishedEvent

    private var _countDownTimer: CountDownTimer? = null

    //#region Public Functions
    fun startTimer(timerID: Int?) {
        val timer = _timers.value.find { it.id == timerID } ?: return

        _countDownTimer?.cancel()
        _activeTimerID.value = timerID ?: -1

        val updatedTimer = timer.copy(isActive = true)
        _timers.value = _timers.value.map { if (it.id == timer.id) updatedTimer else it}

        _countDownTimer = createTimer(updatedTimer).also { it.start() }
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

                if (_timers.value.size >= activeTimerID.value) {
                    _activeTimerID.value = _activeTimerID.value + 1
                }

                viewModelScope.launch {
                    _timerFinishedEvent.emit(Unit)
                }
            }
        }
    }
    //#endregion

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {}

}