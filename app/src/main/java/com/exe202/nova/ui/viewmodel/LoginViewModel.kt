package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.AppRole
import com.exe202.nova.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val usernameOrEmail: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: AppRole? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUsernameOrEmailChange(value: String) {
        _uiState.update { it.copy(usernameOrEmail = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun toggleShowPassword() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun login() {
        val state = _uiState.value
        if (state.usernameOrEmail.isBlank()) {
            _uiState.update { it.copy(error = "Vui long nhap email hoac ten dang nhap") }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(error = "Vui long nhap mat khau") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.login(state.usernameOrEmail, state.password)
                val user = authRepository.getMe()
                _uiState.update { it.copy(isLoading = false, loginSuccess = user.role) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Dang nhap that bai. Vui long kiem tra lai thong tin."
                    )
                }
            }
        }
    }
}
