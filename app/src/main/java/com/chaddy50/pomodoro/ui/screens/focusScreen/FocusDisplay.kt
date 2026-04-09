package com.chaddy50.pomodoro.ui.screens.focusScreen

import android.content.Context
import android.content.res.Configuration
import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls.MediaControlsDisplay
import com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls.MediaControlsUiState
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerDisplay
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerUiState
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

private const val CONTROLS_AUTO_HIDE_DELAY_MS = 3000L

@Composable
fun FocusDisplay(
    timerUiState: TimerUiState,
    musicUiState: MediaControlsUiState,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
) {
    val context = LocalContext.current
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val timerLabel = getTimerLabel(context, timerUiState)

    // Controls (stop button) are hidden while the timer is active and revealed on tap.
    // When the timer is inactive the button is always visible.
    var controlsVisible by remember { mutableStateOf(false) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    val buttonsVisible = !timerUiState.isTimerActive || controlsVisible

    // Hide controls whenever the timer starts.
    LaunchedEffect(timerUiState.isTimerActive) {
        controlsVisible = false
    }

    // Auto-hide 3 seconds after the last tap. Resets whenever lastTapTime changes.
    LaunchedEffect(lastTapTime) {
        if (lastTapTime > 0L) {
            delay(CONTROLS_AUTO_HIDE_DELAY_MS)
            controlsVisible = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLandscape) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (timerLabel.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(timerLabel, fontSize = 22.sp)
                    }
                }

                Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    TimerDisplay(
                        uiState = timerUiState,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                    MediaControlsDisplay(
                        uiState = musicUiState,
                        onPlayPause = onPlayPause,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                }

            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (timerLabel.isNotEmpty()) {
                        Text(timerLabel, fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    TimerDisplay(uiState = timerUiState)
                }
                MediaControlsDisplay(
                    uiState = musicUiState,
                    onPlayPause = onPlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )
            }
        }

        // Button floats over the content so nothing shifts when it appears/disappears.
        AnimatedVisibility(
            visible = buttonsVisible,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            TimerButton(timerUiState, onStartTimer, onStopTimer)
        }

        // Full-screen tap target — only present when the controls are hidden.
        // Once the controls appear this is removed, so the stop button is directly tappable.
        if (!buttonsVisible) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            controlsVisible = true
                            lastTapTime = System.currentTimeMillis()
                        }
                    }
            )
        }
    }
}

//#region Private Functions
private fun getTimerLabel(context: Context, uiState: TimerUiState): String {
    return when (uiState.timerType) {
        TimerType.FocusUntil -> "Focusing until ${formatTimeForDisplay(context, uiState.sessionEndTimeInMilliseconds)}"
        else -> "Take a break"
    }
}

internal fun formatTimeForDisplay(context: Context, timeInMilliseconds: Long): String {
    val dateFormatter = DateFormat.getTimeFormat(context)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMilliseconds
    return dateFormatter.format(calendar.time)
}
//#endregion

@Preview
@Composable
private fun TimerDisplayPreview() {
    FocusDisplay(
        timerUiState = TimerUiState(
            isTimerActive = true,
            timerType = TimerType.FocusUntil,
            timeLeftInMilliseconds = TimeUnit.MINUTES.toMillis(25),
            timerLengthInMilliseconds = TimeUnit.MINUTES.toMillis(25),
            focusUntilTimeInMilliseconds = Calendar.getInstance().timeInMillis + TimeUnit.MINUTES.toMillis(
                25
            ),
            sessionEndTimeInMilliseconds = Calendar.getInstance().timeInMillis + TimeUnit.HOURS.toMillis(
                2
            ),
            sessionSegmentCount = 4,
            currentSegmentIndex = 1,
        ),
        musicUiState = MediaControlsUiState(),
        onStartTimer = {},
        onStopTimer = {},
        onPlayPause = {},
        onNext = {},
        onPrevious = {},
    )
}
