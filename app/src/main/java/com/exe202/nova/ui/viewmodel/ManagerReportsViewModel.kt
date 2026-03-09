package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.BookingStatsData
import com.exe202.nova.data.model.RevenueData
import com.exe202.nova.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

data class ManagerReportsUiState(
    val revenueData: List<RevenueData> = emptyList(),
    val bookingStats: List<BookingStatsData> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ManagerReportsViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerReportsUiState())
    val uiState: StateFlow<ManagerReportsUiState> = _uiState

    init {
        loadReports()
    }

    fun loadReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                supervisorScope {
                    val revenueDeferred = async { repository.getRevenueData() }
                    val statsDeferred = async { repository.getBookingStats() }
                    _uiState.update {
                        it.copy(
                            revenueData = revenueDeferred.await(),
                            bookingStats = statsDeferred.await(),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
