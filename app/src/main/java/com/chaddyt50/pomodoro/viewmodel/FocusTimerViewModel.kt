package com.chaddyt50.pomodoro.viewmodel

import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

const val COUNT_DOWN_INTERVAL_SECOND: Long = 1000

class FocusTimerViewModel : ViewModel() {
    private val _focusTimeInMilliseconds = mutableLongStateOf(30000)

    private val _timeLeftInMilliseconds = mutableLongStateOf(0)

    private val _isTimerActive = mutableStateOf(false)
    val isTimerActive: State<Boolean> = _isTimerActive

    private val _timeLeftText = mutableStateOf((_focusTimeInMilliseconds.longValue / 1000).toString())
    val timeLeftText: State<String> = _timeLeftText

    private val timer =
        object : CountDownTimer(_focusTimeInMilliseconds.longValue, COUNT_DOWN_INTERVAL_SECOND) {
            override fun onTick(millisecondsUntilFinished: Long) {
                val secondsUntilFinished = (millisecondsUntilFinished / 1000)
                _timeLeftInMilliseconds.longValue = secondsUntilFinished
                _timeLeftText.value = secondsUntilFinished.toString()
            }

            override fun onFinish() {
                _isTimerActive.value = false
                _timeLeftText.value = "Timer finished"
            }
        }

    fun startTimer() {
        if (!_isTimerActive.value) {
            _isTimerActive.value = true
            _timeLeftInMilliseconds.longValue = _focusTimeInMilliseconds.longValue
            timer.start()
        }
    }
}