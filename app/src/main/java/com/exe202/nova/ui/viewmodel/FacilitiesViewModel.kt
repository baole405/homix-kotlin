package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.exe202.nova.data.model.BBQSlot
import com.exe202.nova.data.model.ParkingSlot
import com.exe202.nova.data.model.PoolSlot
import com.exe202.nova.data.repository.FacilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class FacilitiesUiState(
    val poolSlots: List<PoolSlot> = emptyList(),
    val bbqSlots: List<BBQSlot> = emptyList(),
    val parkingSlots: List<ParkingSlot> = emptyList()
)

@HiltViewModel
class FacilitiesViewModel @Inject constructor(
    private val repository: FacilityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FacilitiesUiState(
            poolSlots = repository.getPoolSlots(),
            bbqSlots = repository.getBbqSlots(),
            parkingSlots = repository.getParkingSlots()
        )
    )
    val uiState: StateFlow<FacilitiesUiState> = _uiState
}
