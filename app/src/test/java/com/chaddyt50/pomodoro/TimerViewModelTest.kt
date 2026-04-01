package com.chaddyt50.pomodoro

import com.chaddy50.pomodoro.ui.screens.timerScreen.TimerViewModel
import org.junit.Test

import org.junit.Assert.*

class TimerViewModelTest {

    private val viewModel = TimerViewModel()
    private val todayAt0800 = 1726405200000 // 09/15/24 0800
    private val todayAt0820 = 1726406400000 // 09/15/24 0820
    private val todayAt0825 = 1726406700000 // 09/15/24 0825
    private val todayAt0855 = 1726408500000 // 09/15/24 0855

    @Test
    fun getFocusUntilTimeInMilliseconds_25Minutes() {
        assertEquals(todayAt0825, viewModel.getFocusUntilTimeInMilliseconds(todayAt0800))
    }

    @Test
    fun getFocusUntilTimeInMilliseconds_5Minutes() {
        assertEquals(todayAt0855, viewModel.getFocusUntilTimeInMilliseconds(todayAt0820))
    }
}