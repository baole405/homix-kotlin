package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Bill
import com.exe202.nova.data.model.Notification
import com.exe202.nova.data.model.User
import com.exe202.nova.data.repository.AuthRepository
import com.exe202.nova.data.repository.BillRepository
import com.exe202.nova.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val user: User? = null,
    val upcomingBills: List<Bill> = emptyList(),
    val recentNotifications: List<Notification> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val billRepository: BillRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val userDeferred = async { authRepository.getMe() }
                val billsDeferred = async { billRepository.getUpcomingBills() }
                val notificationsDeferred = async { notificationRepository.getNotifications() }

                _uiState.update {
                    it.copy(
                        user = userDeferred.await(),
                        upcomingBills = billsDeferred.await().take(3),
                        recentNotifications = notificationsDeferred.await().take(3),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Loi tai du lieu") }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val userDeferred = async { authRepository.getMe() }
                val billsDeferred = async { billRepository.getUpcomingBills() }
                val notificationsDeferred = async { notificationRepository.getNotifications() }

                _uiState.update {
                    it.copy(
                        user = userDeferred.await(),
                        upcomingBills = billsDeferred.await().take(3),
                        recentNotifications = notificationsDeferred.await().take(3),
                        isRefreshing = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false, error = e.message) }
            }
        }
    }
}
