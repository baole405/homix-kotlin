package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.exe202.nova.data.model.MaintenanceRequest
import com.exe202.nova.data.repository.MaintenanceRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MaintenanceUiState(
    val requests: List<MaintenanceRequest> = emptyList(),
    val showForm: Boolean = false,
    val formTitle: String = "",
    val formDescription: String = "",
    val formLocation: String = "",
    val formError: String? = null
)

@HiltViewModel
class MaintenanceRequestViewModel @Inject constructor(
    private val repository: MaintenanceRequestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaintenanceUiState(requests = repository.getAll()))
    val uiState: StateFlow<MaintenanceUiState> = _uiState

    fun showForm() = _uiState.update { it.copy(showForm = true, formTitle = "", formDescription = "", formLocation = "", formError = null) }
    fun hideForm() = _uiState.update { it.copy(showForm = false) }
    fun onTitleChange(v: String) = _uiState.update { it.copy(formTitle = v) }
    fun onDescriptionChange(v: String) = _uiState.update { it.copy(formDescription = v) }
    fun onLocationChange(v: String) = _uiState.update { it.copy(formLocation = v) }

    fun submit() {
        val state = _uiState.value
        if (state.formTitle.isBlank()) { _uiState.update { it.copy(formError = "Vui lòng nhập tiêu đề") }; return }
        if (state.formDescription.isBlank()) { _uiState.update { it.copy(formError = "Vui lòng nhập mô tả") }; return }
        if (state.formLocation.isBlank()) { _uiState.update { it.copy(formError = "Vui lòng nhập vị trí") }; return }
        repository.create(state.formTitle, state.formDescription, state.formLocation)
        _uiState.update { it.copy(requests = repository.getAll(), showForm = false) }
    }
}
