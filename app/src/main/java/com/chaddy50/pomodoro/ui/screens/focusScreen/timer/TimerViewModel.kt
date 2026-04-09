package com.chaddy50.pomodoro.ui.screens.focusScreen.timer

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaddy50.pomodoro.ui.screens.focusScreen.PomodoroTimer
import com.chaddy50.pomodoro.ui.screens.focusScreen.TimerType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

private val COUNT_DOWN_INTERVAL_SECONDS = TimeUnit.SECONDS.toMillis(1)

private val FOCUS_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(25)
private val SHORT_BREAK_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(5)
private val LONG_BREAK_TIME_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(15)
private val SHORT_BREAKS_BEFORE_LONG_BREAK = 2
private val MINIMUM_LAST_FOCUS_IN_MILLISECONDS = TimeUnit.MINUTES.toMillis(10)
// Minimum viable pair: minimum focus + short break
private val MIN_PAIR_IN_MILLISECONDS = MINIMUM_LAST_FOCUS_IN_MILLISECONDS + SHORT_BREAK_TIME_IN_MILLISECONDS
private val DEFAULT_SESSION_LENGTH_IN_MILLISECONDS = TimeUnit.HOURS.toMillis(2)

class TimerViewModel : ViewModel() {
    //#region Properties
    private val _timers = MutableStateFlow(listOf<PomodoroTimer>())
    private val _activeTimerID = MutableStateFlow(0)
    private val _sessionEndTimeEpochMillis = MutableStateFlow(0L)
    private val _sessionFocusCount = MutableStateFlow(0)
    private val _currentFocusIndex = MutableStateFlow(0)

    val uiState = combine(
        _timers,
        _activeTimerID,
        _sessionEndTimeEpochMillis,
        _sessionFocusCount,
        _currentFocusIndex,
    ) { timers, activeTimerID, sessionEndTime, focusCount, focusIndex ->
        val activeTimer = timers.find { it.id == activeTimerID }
        // timers[0] is the active timer; timers[1] is the next upcoming timer
        val nextTimer = timers.getOrNull(1)
        TimerUiState(
            isTimerActive = activeTimer?.isActive ?: false,
            timerType = activeTimer?.type ?: TimerType.FocusUntil,
            timeLeftInMilliseconds = activeTimer?.timeLeftInMilliseconds ?: 0L,
            timerLengthInMilliseconds = activeTimer?.lengthInMilliseconds ?: 0L,
            focusUntilTimeInMilliseconds = activeTimer?.focusUntilTimeInMilliseconds ?: 0L,
            sessionEndTimeInMilliseconds = sessionEndTime,
            sessionSegmentCount = focusCount,
            currentSegmentIndex = focusIndex,
            nextTimerType = nextTimer?.type,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimerUiState()
    )

    private val _focusTimerFinishedEvent = MutableSharedFlow<Unit>()
    val focusTimerFinishedEvent: SharedFlow<Unit> = _focusTimerFinishedEvent.asSharedFlow()

    private val _breakTimerFinishedEvent = MutableSharedFlow<Unit>()
    val breakTimerFinishedEvent: SharedFlow<Unit> = _breakTimerFinishedEvent.asSharedFlow()

    private val _sessionFinishedEvent = MutableSharedFlow<Unit>()
    val sessionFinishedEvent: SharedFlow<Unit> = _sessionFinishedEvent.asSharedFlow()

    private var _countDownTimer: CountDownTimer? = null

    private var _maximumTimerID: Int = 0
    //#endregion

    init {
        val defaultEndTime = roundUpToNextHalfHour(Calendar.getInstance().timeInMillis + DEFAULT_SESSION_LENGTH_IN_MILLISECONDS)
        resetSchedule(Calendar.getInstance().timeInMillis, defaultEndTime)
        activateNextTimer()
    }

    //#region Public Functions
    fun startTimer() {
        val timer = _timers.value.find { it.id == _activeTimerID.value } ?: return

        _countDownTimer?.cancel()

        // For focus timers, subtract any delay since the timer became available so the session
        // ends at the originally-anchored wall-clock time. Breaks always run their full duration.
        val timeLeft = if (timer.type == TimerType.FocusUntil) {
            val calculatedTimeLeft = timer.focusUntilTimeInMilliseconds - Calendar.getInstance().timeInMillis
            if (calculatedTimeLeft <= 0) {
                activateNextTimer()
                return
            }
            calculatedTimeLeft
        } else {
            timer.timeLeftInMilliseconds
        }

        val updatedTimer = timer.copy(isActive = true, timeLeftInMilliseconds = timeLeft, lengthInMilliseconds = timeLeft)
        _timers.value = _timers.value.map { if (it.id == timer.id) updatedTimer else it }

        _countDownTimer = createTimer(updatedTimer).also { it.start() }
    }

    fun stopTimer() {
        _countDownTimer?.cancel()
        _countDownTimer = null

        resetSchedule(Calendar.getInstance().timeInMillis, _sessionEndTimeEpochMillis.value)
        activateNextTimer()
    }

    fun setSessionEndTime(endTimeEpochMillis: Long) {
        _countDownTimer?.cancel()
        _countDownTimer = null

        resetSchedule(Calendar.getInstance().timeInMillis, endTimeEpochMillis)
        activateNextTimer()
    }
    //#endregion

    //#region Private Functions
    private fun resetSchedule(startEpoch: Long, endEpoch: Long) {
        _sessionEndTimeEpochMillis.value = endEpoch
        val schedule = buildSchedule(startEpoch, endEpoch)
        _sessionFocusCount.value = schedule.count { it.type == TimerType.FocusUntil }
        _currentFocusIndex.value = 0
        _activeTimerID.value = 0
        _timers.value = schedule
    }

    internal fun buildSchedule(startEpoch: Long, endEpoch: Long): List<PomodoroTimer> {
        val timers = mutableListOf<PomodoroTimer>()
        var cursor = startEpoch
        var shortBreakCount = 0
        var id = _maximumTimerID

        while (true) {
            val remaining = endEpoch - cursor
            val breakType = if (shortBreakCount >= SHORT_BREAKS_BEFORE_LONG_BREAK) TimerType.LongBreak else TimerType.ShortBreak
            val breakMillis = getBreakTimerLengthInMilliseconds(breakType)

            // Is there enough room for a standard pair + at least one more minimum pair after?
            val isLastPair = remaining < FOCUS_TIME_IN_MILLISECONDS + breakMillis + MIN_PAIR_IN_MILLISECONDS

            if (isLastPair) {
                val focusDuration = remaining - breakMillis
                if (focusDuration < MINIMUM_LAST_FOCUS_IN_MILLISECONDS) break
                timers +=PomodoroTimer.Companion.create(++id, TimerType.FocusUntil, focusDuration, cursor + focusDuration)
                timers +=PomodoroTimer.Companion.create(++id, breakType, breakMillis)
                break
            } else {
                timers +=PomodoroTimer.Companion.create(++id, TimerType.FocusUntil, FOCUS_TIME_IN_MILLISECONDS, cursor + FOCUS_TIME_IN_MILLISECONDS)
                timers +=PomodoroTimer.Companion.create(++id, breakType, breakMillis)
                cursor += FOCUS_TIME_IN_MILLISECONDS + breakMillis
                if (breakType == TimerType.ShortBreak) shortBreakCount++ else shortBreakCount = 0
            }
        }

        _maximumTimerID = id
        return timers
    }

    private fun activateNextTimer() {
        val currentIndex = _timers.value.indexOfFirst { it.id == _activeTimerID.value }
        val nextTimer = _timers.value.getOrNull(currentIndex + 1)
        if (nextTimer != null) {
            _activeTimerID.value = nextTimer.id
            _timers.value = _timers.value.filter { it.id >= nextTimer.id }
            if (nextTimer.type == TimerType.FocusUntil) {
                _currentFocusIndex.value++
                // Re-anchor the focus end time to now so the drift window is only the delay
                // between this timer becoming available and the user pressing play, not the
                // entire time since the schedule was built.
                val now = Calendar.getInstance().timeInMillis
                _timers.value = _timers.value.map {
                    if (it.id == nextTimer.id) it.copy(focusUntilTimeInMilliseconds = now + it.timeLeftInMilliseconds)
                    else it
                }
            }
        } else {
            // Session finished — notify and rebuild for the same end time
            viewModelScope.launch { _sessionFinishedEvent.emit(Unit) }
            val schedule = buildSchedule(Calendar.getInstance().timeInMillis, _sessionEndTimeEpochMillis.value)
            if (schedule.isEmpty()) return
            _sessionFocusCount.value = schedule.count { it.type == TimerType.FocusUntil }
            _currentFocusIndex.value = 0
            _activeTimerID.value = 0
            _timers.value = schedule
            activateNextTimer()
        }
    }

    private fun createTimer(timer: PomodoroTimer): CountDownTimer {
        return object : CountDownTimer(
            timer.timeLeftInMilliseconds,
            COUNT_DOWN_INTERVAL_SECONDS
        ) {
            override fun onTick(millisecondsUntilFinished: Long) {
                val updatedTimer = timer.copy(timeLeftInMilliseconds = millisecondsUntilFinished)
                _timers.value = _timers.value.map { if (it.id == timer.id) updatedTimer else it }
            }

            override fun onFinish() {
                val finishedTimer = timer.copy(timeLeftInMilliseconds = 0)
                _timers.value = _timers.value.map { if (it.id == timer.id) finishedTimer else it }

                when (timer.type) {
                    TimerType.FocusUntil -> {
                        activateNextTimer()
                        viewModelScope.launch {
                            _focusTimerFinishedEvent.emit(Unit)
                        }
                    }
                    else -> {
                        activateNextTimer()
                        viewModelScope.launch {
                            _breakTimerFinishedEvent.emit(Unit)
                        }
                    }
                }
            }
        }
    }

    private fun roundUpToNextHalfHour(epochMillis: Long): Long {
        val halfHourMillis = TimeUnit.MINUTES.toMillis(30)
        val millisIntoCurrentHalfHour = epochMillis % halfHourMillis
        return if (millisIntoCurrentHalfHour == 0L) epochMillis
               else epochMillis + (halfHourMillis - millisIntoCurrentHalfHour)
    }

    private fun getBreakTimerLengthInMilliseconds(breakTimerType: TimerType): Long {
        return when (breakTimerType) {
            TimerType.LongBreak -> LONG_BREAK_TIME_IN_MILLISECONDS
            TimerType.ShortBreak -> SHORT_BREAK_TIME_IN_MILLISECONDS
            else -> 0
        }
    }
    //#endregion
}
