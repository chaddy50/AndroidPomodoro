package com.chaddyt50.pomodoro.model

import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.TimeUnit

val COUNT_DOWN_INTERVAL_SECONDS = TimeUnit.SECONDS.toMillis(1)

class Timer(timerLengthInMilliseconds: Long, onFinish: ()->Unit) {
    private val _timeLeftInMilliseconds = mutableLongStateOf(timerLengthInMilliseconds)
    val timeLeftInMilliseconds: State<Long> = _timeLeftInMilliseconds

    private val _isActive = mutableStateOf(false)
    val isActive: State<Boolean> = _isActive

    private val _timer =
        object : CountDownTimer(
            _timeLeftInMilliseconds.longValue,
            COUNT_DOWN_INTERVAL_SECONDS
        ) {
            override fun onTick(millisecondsUntilFinished: Long) {
                _timeLeftInMilliseconds.longValue = millisecondsUntilFinished
            }

            override fun onFinish() {
                _isActive.value = false
                onFinish()
            }
        }

    fun start() {
        if (!_isActive.value) {
            _isActive.value = true
            _timer.start()
        }
    }
}