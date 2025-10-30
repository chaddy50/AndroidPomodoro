package com.chaddy50.pomodoro.view.TimerScreen

import android.app.Activity
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.chaddy50.pomodoro.viewmodel.TimerType
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Preview
@Composable
private fun TimerDisplayPreview() {
    TimerDisplay(
        LocalContext.current,
        false,
        TimeUnit.MINUTES.toMillis(25),
        TimeUnit.MINUTES.toMillis(25),
        TimerType.FocusUntil,
        {},
        Calendar.getInstance().timeInMillis
    )
}

@Composable
fun TimerDisplay(
    context: Context,
    isTimerActive: Boolean,
    timeLeftInMilliseconds: Long,
    timerLengthInMilliseconds: Long,
    timerType: TimerType,
    startTimer: () -> Unit,
    focusUntilTimeInMilliseconds: Long,
) {
    LaunchedEffect(isTimerActive, timerType) {
        if (isTimerActive && (timerType == TimerType.FocusUntil)) {
            hideSystemUI(context)
        }
        else {
            showSystemUI(context)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .fillMaxWidth(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { 1f - (timeLeftInMilliseconds.toFloat() / timerLengthInMilliseconds.toFloat()) },
                modifier = Modifier
                    .matchParentSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 16.dp,
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
                strokeCap = StrokeCap.Round,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    getTimerLabel(
                        timerType,
                        isTimerActive
                    ),
                    fontSize = 30.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-60).dp)
                )

                Text(
                    getTimerDisplay(
                        context,
                        isTimerActive,
                        timerType,
                        timeLeftInMilliseconds,
                        focusUntilTimeInMilliseconds
                    ),
                    fontSize = 75.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

        FilledIconButton(
            onClick = startTimer,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(60.dp)
                .widthIn(60.dp)
                .offset(y = 50.dp)
        ) {
            Icon(
                imageVector = getTimerButtonIcon(isTimerActive),
                contentDescription = "Start timer",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

//#region Private Functions
private fun hideSystemUI(context: Context) {
    if (context is Activity) {
        val window = context.window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // I'm purposefully using the legacy flags because they result in a smoother transition when the view re-sizes
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

private fun showSystemUI(context: Context) {
    if (context is Activity) {
        val window = context.window
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // I'm purposefully using the legacy flags because they result in a smoother transition when the view re-sizes
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

private fun getTimerLabel(
    timerType: TimerType,
    isTimerActive: Boolean,
): String {
    return when (timerType) {
        TimerType.FocusUntil -> {
            return when (isTimerActive) {
                true -> ""
                false -> "Focus until"
            }
        }
        else -> "Take a break"
    }
}

private fun getTimerDisplay(
    context: Context,
    isTimerActive: Boolean,
    timerType: TimerType,
    timeLeftInMilliseconds: Long,
    focusUntilTimeInMilliseconds: Long
): String {
    if ((timerType == TimerType.FocusUntil) && !isTimerActive) {
        return formatTimeForDisplay(context, focusUntilTimeInMilliseconds)
    }
    else {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMilliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
        return "${minutes}:${seconds.toString().padStart(2, '0')}"
    }
}

private fun getTimerButtonIcon(
    isTimerActive: Boolean
): ImageVector {
    return when(isTimerActive) {
        true -> Icons.Filled.Close
        false -> Icons.Filled.PlayArrow
    }
}

private fun formatTimeForDisplay(context: Context, timeInMilliseconds: Long): String {
    val dateFormatter = DateFormat.getTimeFormat(context)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMilliseconds
    return dateFormatter.format(calendar.time)
}
//#endregion