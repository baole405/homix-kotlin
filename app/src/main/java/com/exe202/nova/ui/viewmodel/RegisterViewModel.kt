package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registerSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onFullNameChange(value: String) { _uiState.update { it.copy(fullName = value, error = null) } }
    fun onUsernameChange(value: String) { _uiState.update { it.copy(username = value, error = null) } }
    fun onEmailChange(value: String) { _uiState.update { it.copy(email = value, error = null) } }
    fun onPhoneNumberChange(value: String) { _uiState.update { it.copy(phoneNumber = value, error = null) } }
    fun onPasswordChange(value: String) { _uiState.update { it.copy(password = value, error = null) } }
    fun onConfirmPasswordChange(value: String) { _uiState.update { it.copy(confirmPassword = value, error = null) } }

    fun register() {
        val state = _uiState.value
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(.+)$")

        when {
            state.fullName.isBlank() || state.username.isBlank() || state.email.isBlank() ||
                state.phoneNumber.isBlank() || state.password.isBlank() -> {
                _uiState.update { it.copy(error = "Vui long dien day du thong tin") }
                return
            }
            !emailRegex.matches(state.email) -> {
                _uiState.update { it.copy(error = "Email khong hop le") }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(error = "Mat khau phai co it nhat 6 ky tu") }
                return
            }
            state.confirmPassword != state.password -> {
                _uiState.update { it.copy(error = "Mat khau xac nhan khong khop") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.register(
                    username = state.username,
                    email = state.email,
                    password = state.password,
                    fullName = state.fullName,
                    phoneNumber = state.phoneNumber
                )
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Dang ky that bai. Vui long thu lai.")
                }
            }
        }
    }
}
