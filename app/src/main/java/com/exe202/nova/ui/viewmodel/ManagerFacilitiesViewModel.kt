package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.BBQSlot
import com.exe202.nova.data.model.ParkingSlot
import com.exe202.nova.data.model.PoolSlot
import com.exe202.nova.data.repository.ManagerFacilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

data class ManagerFacilitiesUiState(
    val parkingSlots: List<ParkingSlot> = emptyList(),
    val bbqSlots: List<BBQSlot> = emptyList(),
    val pool: PoolSlot? = null,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ManagerFacilitiesViewModel @Inject constructor(
    private val repository: ManagerFacilityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerFacilitiesUiState())
    val uiState: StateFlow<ManagerFacilitiesUiState> = _uiState

    init {
        loadFacilities()
    }

    fun loadFacilities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                supervisorScope {
                    val parkingDeferred = async { repository.getParkingSlots() }
                    val bbqDeferred = async { repository.getBbqSlots() }
                    val poolDeferred = async { repository.getPool() }
                    _uiState.update {
                        it.copy(
                            parkingSlots = parkingDeferred.await(),
                            bbqSlots = bbqDeferred.await(),
                            pool = poolDeferred.await(),
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

    fun updateParkingSlot(slot: ParkingSlot) {
        viewModelScope.launch {
            try {
                repository.updateParkingSlot(slot)
                loadFacilities()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateBbqSlot(slot: BBQSlot) {
        viewModelScope.launch {
            try {
                repository.updateBbqSlot(slot)
                loadFacilities()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updatePool(pool: PoolSlot) {
        viewModelScope.launch {
            try {
                repository.updatePool(pool)
                loadFacilities()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
