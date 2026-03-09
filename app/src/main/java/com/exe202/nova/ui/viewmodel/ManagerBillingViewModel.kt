package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.model.ManagerBill
import com.exe202.nova.data.repository.ManagerBillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerBillingUiState(
    val bills: List<ManagerBill> = emptyList(),
    val filteredBills: List<ManagerBill> = emptyList(),
    val statusFilter: BillStatus? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedBill: ManagerBill? = null,
    val error: String? = null
)

@HiltViewModel
class ManagerBillingViewModel @Inject constructor(
    private val repository: ManagerBillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerBillingUiState())
    val uiState: StateFlow<ManagerBillingUiState> = _uiState

    init {
        loadBills()
    }

    fun loadBills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val bills = repository.getAllBills()
                _uiState.update { state ->
                    state.copy(bills = bills, isLoading = false).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setStatusFilter(status: BillStatus?) {
        _uiState.update { it.copy(statusFilter = status).applyFilters() }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query).applyFilters() }
    }

    fun selectBill(bill: ManagerBill?) {
        _uiState.update { it.copy(selectedBill = bill) }
    }

    fun markBillPaid(id: String) {
        viewModelScope.launch {
            try {
                repository.updateBillStatus(id, BillStatus.PAID)
                val bills = repository.getAllBills()
                _uiState.update { state ->
                    state.copy(bills = bills, selectedBill = null).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun ManagerBillingUiState.applyFilters(): ManagerBillingUiState {
        val query = searchQuery.lowercase()
        val filtered = bills
            .filter { statusFilter == null || it.status == statusFilter }
            .filter {
                query.isEmpty() ||
                    it.residentName.lowercase().contains(query) ||
                    it.apartmentUnit.lowercase().contains(query) ||
                    it.title.lowercase().contains(query)
            }
        return copy(filteredBills = filtered)
    }
}
