package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.exe202.nova.data.model.BillDetail
import com.exe202.nova.data.model.MarkPaidRequest
import com.exe202.nova.data.model.CreatePaymentLinkRequest
import com.exe202.nova.data.repository.BillRepository
import com.exe202.nova.ui.navigation.BillDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BillDetailUiState(
    val bill: BillDetail? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isMarkingPaid: Boolean = false,
    val isCreatingPaymentLink: Boolean = false,
    val markPaidSuccess: Boolean = false,
    val paymentUrl: String? = null
)

@HiltViewModel
class BillDetailViewModel @Inject constructor(
    private val billRepository: BillRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val billId: Int = savedStateHandle.toRoute<BillDetailRoute>().billId

    private val _uiState = MutableStateFlow(BillDetailUiState())
    val uiState: StateFlow<BillDetailUiState> = _uiState

    init {
        loadBillDetail()
    }

    fun loadBillDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val bill = billRepository.getBillDetail(billId)
                _uiState.update { it.copy(bill = bill, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Loi tai chi tiet hoa don") }
            }
        }
    }

    fun markAsPaid(paymentMethod: String, transactionRef: String?, notes: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMarkingPaid = true) }
            try {
                billRepository.markPaid(billId, MarkPaidRequest(paymentMethod, transactionRef, notes))
                _uiState.update { it.copy(isMarkingPaid = false, markPaidSuccess = true) }
                loadBillDetail()
            } catch (e: Exception) {
                _uiState.update { it.copy(isMarkingPaid = false, error = e.message) }
            }
        }
    }

    fun createPaymentLink() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingPaymentLink = true) }
            try {
                val response = billRepository.createPaymentLink(
                    CreatePaymentLinkRequest(
                        billId = billId,
                        returnUrl = "nova://payment/success",
                        cancelUrl = "nova://payment/cancel"
                    )
                )
                _uiState.update { it.copy(isCreatingPaymentLink = false, paymentUrl = response.checkoutUrl) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isCreatingPaymentLink = false, error = e.message) }
            }
        }
    }

    fun clearPaymentUrl() {
        _uiState.update { it.copy(paymentUrl = null) }
    }
}
