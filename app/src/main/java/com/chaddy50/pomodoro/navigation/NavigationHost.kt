package com.chaddy50.pomodoro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls.MediaControlsViewModel
import com.chaddy50.pomodoro.ui.screens.homeScreen.HomeScreen
import com.chaddy50.pomodoro.ui.screens.focusScreen.FocusScreen
import com.chaddy50.pomodoro.ui.screens.focusScreen.timer.TimerViewModel

@Composable
fun NavigationHost(
    navController: NavHostController,
    viewModel: TimerViewModel,
    musicViewModel: MediaControlsViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        composable<HomeRoute> {
            HomeScreen(
                viewModel = viewModel,
                onStartFocus = {
                    viewModel.startTimer()
                    navController.navigate(FocusRoute)
                },
            )
        }
        composable<FocusRoute> {
            FocusScreen(
                viewModel = viewModel,
                musicViewModel = musicViewModel,
                onStopFocus = {
                    viewModel.stopTimer()
                    navController.popBackStack()
                },
            )
        }
    }
}
