package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.ApartmentStatus
import com.exe202.nova.data.model.ManagerApartment
import com.exe202.nova.data.repository.ManagerApartmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerApartmentsUiState(
    val apartments: List<ManagerApartment> = emptyList(),
    val filteredApartments: List<ManagerApartment> = emptyList(),
    val statusFilter: ApartmentStatus? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ManagerApartmentsViewModel @Inject constructor(
    private val repository: ManagerApartmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerApartmentsUiState())
    val uiState: StateFlow<ManagerApartmentsUiState> = _uiState

    init {
        loadApartments()
    }

    fun loadApartments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val apartments = repository.getAllApartments()
                _uiState.update { state ->
                    state.copy(apartments = apartments, isLoading = false).applyFilters()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setStatusFilter(status: ApartmentStatus?) {
        _uiState.update { it.copy(statusFilter = status).applyFilters() }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query).applyFilters() }
    }

    private fun ManagerApartmentsUiState.applyFilters(): ManagerApartmentsUiState {
        val query = searchQuery.lowercase()
        val filtered = apartments
            .filter { statusFilter == null || it.status == statusFilter }
            .filter {
                query.isEmpty() ||
                    it.unitNumber.lowercase().contains(query) ||
                    (it.residentName?.lowercase()?.contains(query) == true)
            }
        return copy(filteredApartments = filtered)
    }
}
