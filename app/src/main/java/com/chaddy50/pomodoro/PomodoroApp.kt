package com.chaddy50.pomodoro

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.chaddy50.pomodoro.notification.NotificationHandler
import com.chaddy50.pomodoro.notification.createNotificationChannels
import com.chaddy50.pomodoro.ui.theme.PomodoroTheme
import com.chaddy50.pomodoro.view.TimerScreen.TimerScreen
import com.chaddy50.pomodoro.viewmodel.PomodoroViewModel

class PomodoroApp : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        createNotificationChannels(context)
        enableEdgeToEdge()
        val viewModel: PomodoroViewModel by viewModels()
        val notificationHandler = NotificationHandler(context, requestPermissionLauncher)
        lifecycle.addObserver(viewModel)

        if (!notificationHandler.hasPermission()) {
            notificationHandler.requestPermission(this)
        }

        setContent {
            PomodoroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        TimerScreen.Content(
                            viewModel,
                            context,
                            notificationHandler
                        )
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
    }
}