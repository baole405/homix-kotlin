package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Customer
import com.exe202.nova.data.repository.ManagerCustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerCustomersUiState(
    val customers: List<Customer> = emptyList(),
    val filteredCustomers: List<Customer> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val selectedCustomer: Customer? = null,
    val error: String? = null
)

@HiltViewModel
class ManagerCustomersViewModel @Inject constructor(
    private val repository: ManagerCustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerCustomersUiState())
    val uiState: StateFlow<ManagerCustomersUiState> = _uiState

    init {
        loadCustomers()
    }

    fun loadCustomers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val customers = repository.getAllCustomers()
                _uiState.update { state ->
                    state.copy(customers = customers, isLoading = false).applyFilter()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query).applyFilter() }
    }

    private fun ManagerCustomersUiState.applyFilter(): ManagerCustomersUiState {
        val query = searchQuery.lowercase()
        val filtered = if (query.isEmpty()) customers
        else customers.filter {
            it.name.lowercase().contains(query) ||
                it.email.lowercase().contains(query) ||
                (it.apartmentUnit?.lowercase()?.contains(query) == true)
        }
        return copy(filteredCustomers = filtered)
    }
}
