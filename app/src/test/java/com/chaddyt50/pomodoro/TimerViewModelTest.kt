package com.chaddyt50.pomodoro

import com.chaddy50.pomodoro.ui.screens.focusScreen.TimerType
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class TimerViewModelTest {

    private val viewModel = TimerViewModel()

    private fun minutes(n: Long) = TimeUnit.MINUTES.toMillis(n)
    private fun hours(n: Long) = TimeUnit.HOURS.toMillis(n)

    @Test
    fun buildSchedule_standardSession_correctCadence() {
        val start = 0L
        val end = hours(2)  // 120 min
        val schedule = viewModel.buildSchedule(start, end)

        // Focus block lengths should all be 25 min except possibly the last
        val focusBlocks = schedule.filter { it.type == TimerType.FocusUntil }
        val breakBlocks = schedule.filter { it.type != TimerType.FocusUntil }

        // Session must end with a break
        assertEquals(focusBlocks.size, breakBlocks.size)

        // Break cadence: short, short, long, short, short, long...
        var shortBreakCount = 0
        for (block in breakBlocks) {
            if (block.type == TimerType.ShortBreak) {
                shortBreakCount++
            } else {
                // Long break must follow exactly 2 short breaks
                assertEquals(2, shortBreakCount)
                shortBreakCount = 0
            }
        }
    }

    @Test
    fun buildSchedule_totalTimeEqualsSessionLength() {
        val start = 0L
        val end = hours(2) + minutes(13)  // 133 min
        val schedule = viewModel.buildSchedule(start, end)

        val totalTime = schedule.sumOf { it.lengthInMilliseconds }
        assertEquals(end - start, totalTime)
    }

    @Test
    fun buildSchedule_sessionEndsWithBreak() {
        val start = 0L
        val end = hours(2)
        val schedule = viewModel.buildSchedule(start, end)

        assertTrue(schedule.isNotEmpty())
        val lastTimer = schedule.last()
        assertTrue(lastTimer.type == TimerType.ShortBreak || lastTimer.type == TimerType.LongBreak)
    }

    @Test
    fun buildSchedule_shortSession_singlePair() {
        val start = 0L
        val end = minutes(30)  // exactly one standard pair
        val schedule = viewModel.buildSchedule(start, end)

        assertEquals(2, schedule.size)
        assertEquals(TimerType.FocusUntil, schedule[0].type)
        assertEquals(TimerType.ShortBreak, schedule[1].type)
        assertEquals(minutes(25), schedule[0].lengthInMilliseconds)
        assertEquals(minutes(5), schedule[1].lengthInMilliseconds)
    }

    @Test
    fun buildSchedule_tooShortForSession_emptySchedule() {
        val start = 0L
        val end = minutes(12)  // 12 min: focus would be 12-5 = 7 min < 10 min minimum
        val schedule = viewModel.buildSchedule(start, end)

        assertTrue(schedule.isEmpty())
    }

    @Test
    fun buildSchedule_minimumSession_singlePair() {
        val start = 0L
        val end = minutes(15)  // 15 min: focus = 15-5 = 10 min (exactly at minimum)
        val schedule = viewModel.buildSchedule(start, end)

        assertEquals(2, schedule.size)
        assertEquals(TimerType.FocusUntil, schedule[0].type)
        assertEquals(minutes(10), schedule[0].lengthInMilliseconds)
        assertEquals(TimerType.ShortBreak, schedule[1].type)
        assertEquals(minutes(5), schedule[1].lengthInMilliseconds)
    }

    @Test
    fun buildSchedule_lastFocusBlockExtendsToFillRemainder() {
        val start = 0L
        val end = minutes(133)  // 133 min, last pair focus should be 28 min
        val schedule = viewModel.buildSchedule(start, end)

        val lastFocusBlock = schedule.last { it.type == TimerType.FocusUntil }
        // Last focus block should NOT be 25 min (it fills remaining time)
        assertEquals(minutes(28), lastFocusBlock.lengthInMilliseconds)
    }

    @Test
    fun buildSchedule_nonLastFocusBlocksAre25Minutes() {
        val start = 0L
        val end = minutes(133)
        val schedule = viewModel.buildSchedule(start, end)

        val focusBlocks = schedule.filter { it.type == TimerType.FocusUntil }
        // All but the last focus block should be 25 min
        focusBlocks.dropLast(1).forEach { block ->
            assertEquals(minutes(25), block.lengthInMilliseconds)
        }
    }
}
