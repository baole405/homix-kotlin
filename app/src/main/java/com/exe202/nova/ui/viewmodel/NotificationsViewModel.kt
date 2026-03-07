package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Notification
import com.exe202.nova.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val unreadCount: Int = 0
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val notifications = notificationRepository.getNotifications()
                _uiState.update {
                    it.copy(
                        notifications = notifications,
                        isLoading = false,
                        unreadCount = notifications.count { n -> !n.isRead }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Loi tai thong bao") }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val notifications = notificationRepository.getNotifications()
                _uiState.update {
                    it.copy(
                        notifications = notifications,
                        isRefreshing = false,
                        unreadCount = notifications.count { n -> !n.isRead }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(id)
                _uiState.update { state ->
                    val updated = state.notifications.map { n ->
                        if (n.id == id) n.copy(isRead = true) else n
                    }
                    state.copy(
                        notifications = updated,
                        unreadCount = updated.count { !it.isRead }
                    )
                }
            } catch (_: Exception) { }
        }
    }

    fun markAllRead() {
        val unread = _uiState.value.notifications.filter { !it.isRead }
        unread.forEach { markAsRead(it.id) }
    }
}
