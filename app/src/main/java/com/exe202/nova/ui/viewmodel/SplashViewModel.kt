package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.AppRole
import com.exe202.nova.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    data object Loading : SplashDestination()
    data object Login : SplashDestination()
    data object Resident : SplashDestination()
    data object Manager : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            val token = authRepository.getToken()
            if (token == null) {
                _destination.value = SplashDestination.Login
                return@launch
            }
            try {
                val user = authRepository.getMe()
                _destination.value = when (user.role) {
                    AppRole.MANAGER -> SplashDestination.Manager
                    AppRole.RESIDENT -> SplashDestination.Resident
                }
            } catch (e: Exception) {
                authRepository.clearToken()
                _destination.value = SplashDestination.Login
            }
        }
    }
}
