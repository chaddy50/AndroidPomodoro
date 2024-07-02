package com.chaddyt50.pomodoro.viewmodel

import android.os.CountDownTimer
import android.widget.NumberPicker
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

const val COUNT_DOWN_INTERVAL_SECONDS: Long = 1000

class FocusTimerViewModel : ViewModel() {
    private val _focusTimeInMinutes = mutableLongStateOf(25)

    private val _timeLeftInMilliseconds =
        mutableLongStateOf(TimeUnit.MINUTES.toMillis(_focusTimeInMinutes.longValue))
    val timeLeftInMilliseconds: State<Long> = _timeLeftInMilliseconds

    private val _isTimerActive = mutableStateOf(false)
    val isTimerActive: State<Boolean> = _isTimerActive

    private val timer =
        object : CountDownTimer(
            minutesToMilliseconds(_focusTimeInMinutes.longValue),
            COUNT_DOWN_INTERVAL_SECONDS
        ) {
            override fun onTick(millisecondsUntilFinished: Long) {
                _timeLeftInMilliseconds.longValue = millisecondsUntilFinished
            }

            override fun onFinish() {
                _isTimerActive.value = false
            }
        }

    private fun minutesToMilliseconds(minutes: Long): Long {
        return TimeUnit.MINUTES.toMillis(minutes);
    }

    fun startTimer() {
        if (!_isTimerActive.value) {
            _isTimerActive.value = true
            _timeLeftInMilliseconds.longValue = minutesToMilliseconds(_focusTimeInMinutes.longValue)
            timer.start()
        }
    }

    fun updateFocusTimeInMinutes(newValue: Long): Unit {
        _focusTimeInMinutes.longValue = newValue
    }
}