package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.model.ManagerBooking
import com.exe202.nova.data.model.ServiceType
import com.exe202.nova.data.repository.ManagerBookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerBookingsUiState(
    val bookings: List<ManagerBooking> = emptyList(),
    val filteredBookings: List<ManagerBooking> = emptyList(),
    val statusFilter: BookingStatus? = null,
    val serviceTypeFilter: ServiceType? = null,
    val isLoading: Boolean = true,
    val selectedBooking: ManagerBooking? = null,
    val isUpdating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ManagerBookingsViewModel @Inject constructor(
    private val repository: ManagerBookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerBookingsUiState())
    val uiState: StateFlow<ManagerBookingsUiState> = _uiState

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bookings = repository.getAllBookings()
                _uiState.update { state ->
                    state.copy(bookings = bookings, isLoading = false).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setStatusFilter(status: BookingStatus?) {
        _uiState.update { it.copy(statusFilter = status).applyFilters() }
    }

    fun setServiceTypeFilter(type: ServiceType?) {
        _uiState.update { it.copy(serviceTypeFilter = type).applyFilters() }
    }

    fun selectBooking(booking: ManagerBooking?) {
        _uiState.update { it.copy(selectedBooking = booking) }
    }

    fun approveBooking(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            try {
                repository.updateBookingStatus(id, BookingStatus.CONFIRMED)
                val bookings = repository.getAllBookings()
                _uiState.update { state ->
                    state.copy(
                        bookings = bookings,
                        isUpdating = false,
                        selectedBooking = null
                    ).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    fun rejectBooking(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            try {
                repository.updateBookingStatus(id, BookingStatus.REJECTED)
                val bookings = repository.getAllBookings()
                _uiState.update { state ->
                    state.copy(
                        bookings = bookings,
                        isUpdating = false,
                        selectedBooking = null
                    ).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    fun cancelBooking(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            try {
                repository.updateBookingStatus(id, BookingStatus.CANCELLED)
                val bookings = repository.getAllBookings()
                _uiState.update { state ->
                    state.copy(
                        bookings = bookings,
                        isUpdating = false,
                        selectedBooking = null
                    ).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isUpdating = false, error = e.message) }
            }
        }
    }

    fun saveNotes(id: Int, notes: String) {
        viewModelScope.launch {
            try {
                repository.updateBookingNotes(id, notes)
                val bookings = repository.getAllBookings()
                _uiState.update { state ->
                    state.copy(bookings = bookings).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun ManagerBookingsUiState.applyFilters(): ManagerBookingsUiState {
        val filtered = bookings
            .filter { statusFilter == null || it.status == statusFilter }
            .filter { serviceTypeFilter == null || it.serviceType == serviceTypeFilter }
        return copy(filteredBookings = filtered)
    }
}
