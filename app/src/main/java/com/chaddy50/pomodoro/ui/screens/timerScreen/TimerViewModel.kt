package com.chaddy50.pomodoro.ui.screens.timerScreen

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

private val COUNT_DOWN_INTERVAL_SECONDS = TimeUnit.SECONDS.toMillis(1)
private val HALF_HOUR_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(1) / 2
private val MINIMUM_FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(15)
private val SHORT_BREAK_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(5)
private val LONG_BREAK_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)

class TimerViewModel : ViewModel() {
    //#region Properties
    private val _timers = MutableStateFlow(listOf<PomodoroTimer>())
    private val _activeTimerID = MutableStateFlow(0)

    val uiState = combine(_timers, _activeTimerID) { timers, activeTimerID ->
        val activeTimer = timers.find { it.id == activeTimerID }
        TimerUiState(
            isTimerActive = activeTimer?.isActive ?: false,
            timerType = activeTimer?.type ?: TimerType.FocusUntil,
            timeLeftInMilliseconds = activeTimer?.timeLeftInMilliseconds ?: 0L,
            timerLengthInMilliseconds = activeTimer?.lengthInMilliseconds ?: 0L,
            focusUntilTimeInMilliseconds = activeTimer?.focusUntilTimeInMilliseconds ?: 0L,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimerUiState()
    )

    val focusTimerFinishedEvent = MutableSharedFlow<Unit>()
    val breakTimerFinishedEvent = MutableSharedFlow<Unit>()

    private var _countDownTimer: CountDownTimer? = null

    private var _maximumTimerID: Int = 0
    //#endregion

    init {
        addNextTimerAndBreak()
        activateNextTimer()
    }

    //#region Public Functions
    fun startTimer() {
        val timer = _timers.value.find { it.id == _activeTimerID.value } ?: return

        _countDownTimer?.cancel()

        val updatedTimer = timer.copy(isActive = true)
        _timers.value = _timers.value.map { if (it.id == timer.id) updatedTimer else it }

        _countDownTimer = createTimer(updatedTimer).also { it.start() }
    }

    private fun addNextTimerAndBreak() {
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

    private fun activateNextTimer() {
        val currentIndex = _timers.value.indexOfFirst { it.id == _activeTimerID.value }
        val nextTimer = _timers.value.getOrNull(currentIndex + 1)
        if (nextTimer != null) {
            _activeTimerID.value = nextTimer.id
            _timers.value = _timers.value.filter { it.id >= nextTimer.id }
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
                val finishedTimer = timer.copy(timeLeftInMilliseconds = 0)
                _timers.value = _timers.value.map { if (it.id == timer.id) finishedTimer else it }

                when (timer.type) {
                    TimerType.FocusUntil -> {
                        activateNextTimer()
                        viewModelScope.launch {
                            focusTimerFinishedEvent.emit(Unit)
                        }
                    }
                    else -> {
                        addNextTimerAndBreak()
                        activateNextTimer()
                        viewModelScope.launch {
                            breakTimerFinishedEvent.emit(Unit)
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

    internal fun getFocusUntilTimeInMilliseconds(
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

}