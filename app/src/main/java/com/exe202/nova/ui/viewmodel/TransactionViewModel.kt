package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Transaction
import com.exe202.nova.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM")

data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val selectedMonth: String = YearMonth.now().format(MONTH_FORMATTER),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState

    private var loadJob: Job? = null

    init { loadTransactions() }

    fun loadTransactions() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getTransactionsByMonth(_uiState.value.selectedMonth)
                .onSuccess { list -> _uiState.update { it.copy(transactions = list, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            repository.getTransactionsByMonth(_uiState.value.selectedMonth)
                .onSuccess { list -> _uiState.update { it.copy(transactions = list, isRefreshing = false) } }
                .onFailure { _uiState.update { it.copy(isRefreshing = false) } }
        }
    }

    fun selectMonth(month: String) {
        _uiState.update { it.copy(selectedMonth = month) }
        loadTransactions()
    }

    fun previousMonth() {
        val current = YearMonth.parse(_uiState.value.selectedMonth, MONTH_FORMATTER)
        selectMonth(current.minusMonths(1).format(MONTH_FORMATTER))
    }

    fun nextMonth() {
        val current = YearMonth.parse(_uiState.value.selectedMonth, MONTH_FORMATTER)
        val next = current.plusMonths(1)
        if (!next.isAfter(YearMonth.now())) selectMonth(next.format(MONTH_FORMATTER))
    }
}
