package com.chaddy50.pomodoro.ui.screens.focusScreen.mediaControls

import android.app.Application
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaControlsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MediaControlsUiState())
    val uiState: StateFlow<MediaControlsUiState> = _uiState.asStateFlow()

    private var activeController: MediaController? = null

    private val controllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updateFromController()
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateFromController()
        }
    }

    init {
        viewModelScope.launch {
            MediaNotificationListenerService.Companion.activeControllers.collect { controllers ->
                activeController?.unregisterCallback(controllerCallback)
                activeController = controllers.firstOrNull()
                activeController?.registerCallback(controllerCallback)
                updateFromController()
            }
        }
        checkPermission()
    }

    fun checkPermission() {
        _uiState.value = _uiState.value.copy(hasPermission = hasNotificationListenerPermission())
    }

    fun playPause() {
        val controller = activeController ?: return
        if (controller.playbackState?.state == PlaybackState.STATE_PLAYING) {
            controller.transportControls.pause()
        } else {
            controller.transportControls.play()
        }
    }

    fun next() {
        activeController?.transportControls?.skipToNext()
    }

    fun previous() {
        activeController?.transportControls?.skipToPrevious()
    }

    private fun updateFromController() {
        val controller = activeController
        _uiState.value = _uiState.value.copy(
            hasActiveSession = controller != null,
            isPlaying = controller?.playbackState?.state == PlaybackState.STATE_PLAYING,
            trackTitle = controller?.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "",
            artistName = controller?.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "",
        )
    }

    private fun hasNotificationListenerPermission(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(getApplication<Application>().packageName) == true
    }

    override fun onCleared() {
        super.onCleared()
        activeController?.unregisterCallback(controllerCallback)
    }
}