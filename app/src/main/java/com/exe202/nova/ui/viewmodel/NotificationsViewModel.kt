package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.model.Notification
import com.exe202.nova.data.repository.AppNotificationRepository
import com.exe202.nova.data.repository.BillRepository
import com.exe202.nova.data.repository.BookingRepository
import com.exe202.nova.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    private val notificationRepository: NotificationRepository,
    private val billRepository: BillRepository,
    private val bookingRepository: BookingRepository,
    private val appNotificationRepository: AppNotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    private var remoteNotifications: List<Notification> = emptyList()

    init {
        observeLocalNotifications()
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            refreshFallbackNotifications()

            val remoteError = runCatching {
                remoteNotifications = notificationRepository.getNotifications()
            }.exceptionOrNull()

            applyMergedState(
                isLoading = false,
                error = if (remoteNotifications.isEmpty()) remoteError?.message else null
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            refreshFallbackNotifications()

            val remoteError = runCatching {
                remoteNotifications = notificationRepository.getNotifications()
            }.exceptionOrNull()

            applyMergedState(
                isRefreshing = false,
                error = if (remoteNotifications.isEmpty()) remoteError?.message else null
            )
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            if (id < 0 && appNotificationRepository.markAsReadById(id)) {
                applyMergedState()
                return@launch
            }

            try {
                notificationRepository.markAsRead(id)
                remoteNotifications = remoteNotifications.map { n ->
                    if (n.id == id) n.copy(isRead = true) else n
                }
                applyMergedState()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Loi cap nhat thong bao") }
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            appNotificationRepository.markAllRead()

            remoteNotifications.filter { !it.isRead }.forEach { notification ->
                runCatching { notificationRepository.markAsRead(notification.id) }
            }
            remoteNotifications = remoteNotifications.map { it.copy(isRead = true) }
            applyMergedState()
        }
    }

    private fun observeLocalNotifications() {
        viewModelScope.launch {
            appNotificationRepository.localNotifications.collect {
                applyMergedState()
            }
        }
    }

    private suspend fun refreshFallbackNotifications() {
        val unpaidBills = runCatching { billRepository.getBills() }
            .getOrDefault(emptyList())
            .count { it.status == BillStatus.PENDING || it.status == BillStatus.OVERDUE }

        if (unpaidBills > 0) {
            appNotificationRepository.upsert(
                key = KEY_UNPAID_BILLS,
                title = "Hoa don chua thanh toan",
                content = "Ban co $unpaidBills hoa don chua thanh toan. Vui long kiem tra tab Hoa don.",
                type = "billing"
            )
        } else {
            appNotificationRepository.remove(KEY_UNPAID_BILLS)
        }

        val today = LocalDate.now().toString()
        val todayBookings = runCatching { bookingRepository.getMyBookings() }
            .getOrDefault(emptyList())
            .count {
                it.date == today &&
                    (it.status == BookingStatus.PENDING || it.status == BookingStatus.CONFIRMED)
            }

        if (todayBookings > 0) {
            appNotificationRepository.upsert(
                key = KEY_TODAY_BOOKINGS,
                title = "Lich dat cho hom nay",
                content = "Ban co $todayBookings lich dat cho hom nay. Hay kiem tra chi tiet trong Lich su dat cho.",
                type = "booking"
            )
        } else {
            appNotificationRepository.remove(KEY_TODAY_BOOKINGS)
        }
    }

    private fun applyMergedState(
        isLoading: Boolean = _uiState.value.isLoading,
        isRefreshing: Boolean = _uiState.value.isRefreshing,
        error: String? = _uiState.value.error
    ) {
        val merged = (remoteNotifications + appNotificationRepository.asNotifications())
            .distinctBy { it.id }
            .sortedByDescending { it.createdAt ?: "" }

        _uiState.update {
            it.copy(
                notifications = merged,
                unreadCount = merged.count { n -> !n.isRead },
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                error = error
            )
        }
    }

    companion object {
        private const val KEY_UNPAID_BILLS = "fallback_unpaid_bills"
        private const val KEY_TODAY_BOOKINGS = "fallback_today_bookings"
    }
}
