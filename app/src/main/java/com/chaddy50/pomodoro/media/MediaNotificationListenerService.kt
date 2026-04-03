package com.chaddy50.pomodoro.media

import android.content.ComponentName
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.service.notification.NotificationListenerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaNotificationListenerService : NotificationListenerService() {

    companion object {
        private val _activeControllers = MutableStateFlow<List<MediaController>>(emptyList())
        val activeControllers: StateFlow<List<MediaController>> = _activeControllers.asStateFlow()
    }

    private lateinit var mediaSessionManager: MediaSessionManager
    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        _activeControllers.value = controllers ?: emptyList()
    }

    override fun onListenerConnected() {
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        val componentName = ComponentName(this, MediaNotificationListenerService::class.java)
        mediaSessionManager.addOnActiveSessionsChangedListener(sessionListener, componentName)
        _activeControllers.value = mediaSessionManager.getActiveSessions(componentName)
    }

    override fun onListenerDisconnected() {
        mediaSessionManager.removeOnActiveSessionsChangedListener(sessionListener)
        _activeControllers.value = emptyList()
    }
}
