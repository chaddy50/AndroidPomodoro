package com.chaddyt50.pomodoro.model

import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.TimeUnit

val COUNT_DOWN_INTERVAL_SECONDS = TimeUnit.SECONDS.toMillis(1)

class Timer(timerLengthInMilliseconds: Long, onFinish: () -> Unit) {
    //#region Properties
    private val _timeLeftInMilliseconds = mutableLongStateOf(timerLengthInMilliseconds)
    val timeLeftInMilliseconds: State<Long> = _timeLeftInMilliseconds

    private val _isActive = mutableStateOf(false)
    val isActive: State<Boolean> = _isActive

    private val _isFinished = mutableStateOf(false)
    val isFinished: State<Boolean> = _isFinished

    private var _timer = createTimer()

    private val _onFinish = onFinish
    //#endregion

    //#region Public Functions
    fun start() {
        if (!_isActive.value) {
            _isActive.value = true
            _isFinished.value = false
            _timer.start()
        }
    }

    fun updateTimeLeft(newTimeLeftInMilliseconds: Long) {
        _timeLeftInMilliseconds.longValue = newTimeLeftInMilliseconds
        _timer = createTimer()
    }
    //#endregion

    //#region Private Functions
    private fun createTimer(): CountDownTimer {
        return object : CountDownTimer(
            _timeLeftInMilliseconds.longValue,
            COUNT_DOWN_INTERVAL_SECONDS
        ) {
            override fun onTick(millisecondsUntilFinished: Long) {
                _timeLeftInMilliseconds.longValue = millisecondsUntilFinished
            }

            override fun onFinish() {
                _isActive.value = false
                _isFinished.value = true
                _onFinish()
            }
        }
    }
    //#endregion
}