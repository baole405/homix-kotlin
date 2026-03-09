package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.DashboardStats
import com.exe202.nova.data.model.ManagerBill
import com.exe202.nova.data.model.ManagerBooking
import com.exe202.nova.data.repository.ManagerBillRepository
import com.exe202.nova.data.repository.ManagerBookingRepository
import com.exe202.nova.data.mock.MOCK_DASHBOARD_STATS
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.model.BookingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

data class ManagerDashboardUiState(
    val stats: DashboardStats = MOCK_DASHBOARD_STATS,
    val pendingBookings: List<ManagerBooking> = emptyList(),
    val overdueBills: List<ManagerBill> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ManagerDashboardViewModel @Inject constructor(
    private val bookingRepository: ManagerBookingRepository,
    private val billRepository: ManagerBillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerDashboardUiState())
    val uiState: StateFlow<ManagerDashboardUiState> = _uiState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supervisorScope {
                    val bookingsDeferred = async { bookingRepository.getAllBookings() }
                    val billsDeferred = async { billRepository.getAllBills() }

                    val bookings = bookingsDeferred.await()
                    val bills = billsDeferred.await()

                    _uiState.update {
                        it.copy(
                            pendingBookings = bookings.filter { b -> b.status == BookingStatus.PENDING }.take(3),
                            overdueBills = bills.filter { b -> b.status == BillStatus.OVERDUE }.take(3),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                supervisorScope {
                    val bookingsDeferred = async { bookingRepository.getAllBookings() }
                    val billsDeferred = async { billRepository.getAllBills() }

                    val bookings = bookingsDeferred.await()
                    val bills = billsDeferred.await()

                    _uiState.update {
                        it.copy(
                            pendingBookings = bookings.filter { b -> b.status == BookingStatus.PENDING }.take(3),
                            overdueBills = bills.filter { b -> b.status == BillStatus.OVERDUE }.take(3),
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false, error = e.message) }
            }
        }
    }

    fun approveBooking(id: Int) {
        viewModelScope.launch {
            try {
                bookingRepository.updateBookingStatus(id, BookingStatus.CONFIRMED)
                loadDashboard()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun rejectBooking(id: Int) {
        viewModelScope.launch {
            try {
                bookingRepository.updateBookingStatus(id, BookingStatus.REJECTED)
                loadDashboard()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
