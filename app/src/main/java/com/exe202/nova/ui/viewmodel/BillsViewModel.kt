package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Bill
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.repository.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class BillFilter { ALL, PENDING, PAID, OVERDUE }

data class BillsUiState(
    val allBills: List<Bill> = emptyList(),
    val selectedTab: BillFilter = BillFilter.ALL,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class BillsViewModel @Inject constructor(
    private val billRepository: BillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillsUiState())
    val uiState: StateFlow<BillsUiState> = _uiState

    init {
        loadBills()
    }

    fun loadBills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val bills = billRepository.getBills()
                _uiState.update { it.copy(allBills = bills, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Loi tai hoa don") }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val bills = billRepository.getBills()
                _uiState.update { it.copy(allBills = bills, isRefreshing = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun selectTab(filter: BillFilter) {
        _uiState.update { it.copy(selectedTab = filter) }
    }

    fun filteredBills(): List<Bill> {
        val state = _uiState.value
        return when (state.selectedTab) {
            BillFilter.ALL -> state.allBills
            BillFilter.PENDING -> state.allBills.filter { it.status == BillStatus.PENDING }
            BillFilter.PAID -> state.allBills.filter { it.status == BillStatus.PAID }
            BillFilter.OVERDUE -> state.allBills.filter { it.status == BillStatus.OVERDUE }
        }
    }
}
