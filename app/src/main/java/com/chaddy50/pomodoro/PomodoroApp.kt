package com.chaddy50.pomodoro

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.chaddy50.pomodoro.navigation.FocusRoute
import com.chaddy50.pomodoro.navigation.HomeRoute
import com.chaddy50.pomodoro.navigation.NavigationHost
import com.chaddy50.pomodoro.notification.DndManager
import com.chaddy50.pomodoro.notification.NotificationHandler
import com.chaddy50.pomodoro.notification.createNotificationChannels
import com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls.MediaControlsViewModel
import com.chaddy50.pomodoro.ui.theme.PomodoroTheme
import com.chaddy50.pomodoro.ui.screens.focusScreen.TimerType
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerViewModel
import kotlinx.coroutines.launch

class PomodoroApp : ComponentActivity() {
    private val viewModel: TimerViewModel by viewModels()
    private val musicViewModel: MediaControlsViewModel by viewModels()
    private val dndManager by lazy { DndManager(this) }
    private val notificationHandler by lazy { NotificationHandler(this) }
    private var navController: NavHostController? = null

    companion object {
        const val EXTRA_NAVIGATE_TO_FOCUS = "navigate_to_focus"
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(EXTRA_NAVIGATE_TO_FOCUS, false)) {
            navController?.navigate(FocusRoute) {
                popUpTo<HomeRoute>()
                launchSingleTop = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannels(this)
        enableEdgeToEdge()

        setContent {
            PomodoroTheme {
                val navController = rememberNavController().also { this.navController = it }

                LaunchedEffect(Unit) {
                    launch { viewModel.focusTimerFinishedEvent.collect { notificationHandler.sendFocusTimerFinishedNotification() } }
                    launch { viewModel.breakTimerFinishedEvent.collect { notificationHandler.sendBreakTimerFinishedNotification() } }
                    launch {
                        viewModel.sessionFinishedEvent.collect {
                            notificationHandler.sendSessionFinishedNotification()
                            navController.navigate(HomeRoute) {
                                popUpTo<HomeRoute> { inclusive = true }
                            }
                        }
                    }
                    launch {
                        viewModel.uiState.collect { state ->
                            if (state.isTimerActive && state.timerType == TimerType.FocusUntil) {
                                dndManager.enable()
                                hideSystemUI()
                            } else {
                                dndManager.disable()
                                showSystemUI()
                            }
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationHost(
                        navController = navController,
                        viewModel = viewModel,
                        musicViewModel = musicViewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        musicViewModel.checkPermission()
        if (!dndManager.hasPermission()) {
            dndManager.requestPermission(this)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationHandler.hasPermission()) {
            notificationHandler.requestPermission(this, requestPermissionLauncher)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dndManager.disable()
    }

    // I'm purposefully using the legacy flags because they result in a smoother transition when the view re-sizes
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
}
