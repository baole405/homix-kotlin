package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Booking
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BookingStatusFilter { ALL, PENDING, CONFIRMED, REJECTED }

data class MyBookingsUiState(
    val allBookings: List<Booking> = emptyList(),
    val selectedFilter: BookingStatusFilter = BookingStatusFilter.ALL,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val cancellingId: Int? = null
)

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyBookingsUiState())
    val uiState: StateFlow<MyBookingsUiState> = _uiState

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val bookings = bookingRepository.getMyBookings()
                _uiState.update { it.copy(allBookings = bookings, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Loi tai dat cho") }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val bookings = bookingRepository.getMyBookings()
                _uiState.update { it.copy(allBookings = bookings, isRefreshing = false, error = null) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun selectFilter(filter: BookingStatusFilter) = _uiState.update { it.copy(selectedFilter = filter) }

    fun filteredBookings(): List<Booking> {
        val state = _uiState.value
        return when (state.selectedFilter) {
            BookingStatusFilter.ALL -> state.allBookings
            BookingStatusFilter.PENDING -> state.allBookings.filter { it.status == BookingStatus.PENDING }
            BookingStatusFilter.CONFIRMED -> state.allBookings.filter { it.status == BookingStatus.CONFIRMED }
            BookingStatusFilter.REJECTED -> state.allBookings.filter { it.status == BookingStatus.REJECTED }
        }
    }
}
