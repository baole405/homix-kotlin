package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.CreateFeeTypeRequest
import com.exe202.nova.data.model.FeeType
import com.exe202.nova.data.repository.FeeTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeeTypesUiState(
    val feeTypes: List<FeeType> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val editingFeeType: FeeType? = null,
    val formName: String = "",
    val formDescription: String = "",
    val formUnitPrice: String = "",
    val formMeasureUnit: String = "",
    val formIsRecurring: Boolean = false,
    val formError: String? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class ManagerFeeTypesViewModel @Inject constructor(
    private val repository: FeeTypeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeeTypesUiState())
    val uiState: StateFlow<FeeTypesUiState> = _uiState

    init { loadFeeTypes() }

    fun loadFeeTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getFeeTypes()
                .onSuccess { list -> _uiState.update { it.copy(feeTypes = list, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            repository.getFeeTypes()
                .onSuccess { list -> _uiState.update { it.copy(feeTypes = list, isRefreshing = false) } }
                .onFailure { _uiState.update { it.copy(isRefreshing = false) } }
        }
    }

    fun showCreateDialog() = _uiState.update {
        it.copy(
            showCreateDialog = true, editingFeeType = null,
            formName = "", formDescription = "", formUnitPrice = "",
            formMeasureUnit = "", formIsRecurring = false, formError = null
        )
    }

    fun showEditDialog(feeType: FeeType) = _uiState.update {
        it.copy(
            showCreateDialog = true, editingFeeType = feeType,
            formName = feeType.name, formDescription = feeType.description ?: "",
            formUnitPrice = feeType.unitPrice.toString(),
            formMeasureUnit = feeType.measureUnit ?: "",
            formIsRecurring = feeType.isRecurring, formError = null
        )
    }

    fun dismissDialog() = _uiState.update { it.copy(showCreateDialog = false, editingFeeType = null) }

    fun onFormNameChange(v: String) = _uiState.update { it.copy(formName = v) }
    fun onFormDescriptionChange(v: String) = _uiState.update { it.copy(formDescription = v) }
    fun onFormUnitPriceChange(v: String) = _uiState.update { it.copy(formUnitPrice = v) }
    fun onFormMeasureUnitChange(v: String) = _uiState.update { it.copy(formMeasureUnit = v) }
    fun onFormIsRecurringChange(v: Boolean) = _uiState.update { it.copy(formIsRecurring = v) }

    fun saveFeeType() {
        val state = _uiState.value
        if (state.formName.isBlank()) {
            _uiState.update { it.copy(formError = "Tên không được trống") }
            return
        }
        val price = state.formUnitPrice.toDoubleOrNull()
        if (price == null) {
            _uiState.update { it.copy(formError = "Đơn giá phải là số") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, formError = null) }
            val request = CreateFeeTypeRequest(
                name = state.formName,
                description = state.formDescription.ifBlank { null },
                unitPrice = price,
                measureUnit = state.formMeasureUnit.ifBlank { null },
                isRecurring = state.formIsRecurring
            )
            val result = if (state.editingFeeType != null)
                repository.updateFeeType(state.editingFeeType.id, request)
            else
                repository.createFeeType(request)

            result
                .onSuccess { loadFeeTypes(); _uiState.update { it.copy(showCreateDialog = false, isSaving = false) } }
                .onFailure { e -> _uiState.update { it.copy(formError = e.message, isSaving = false) } }
        }
    }

    fun deleteFeeType(id: Int) {
        viewModelScope.launch {
            repository.deleteFeeType(id)
                .onSuccess { loadFeeTypes() }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}
