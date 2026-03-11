package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Apartment
import com.exe202.nova.data.model.User
import com.exe202.nova.data.repository.ApartmentRepository
import com.exe202.nova.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val apartment: Apartment? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apartmentRepository: ApartmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                supervisorScope {
                    val userDeferred = async { authRepository.getMe() }
                    val apartmentDeferred = async { apartmentRepository.getMyApartment() }

                    val user = userDeferred.await()
                    val apartment = apartmentDeferred.await().getOrNull()

                    _uiState.update {
                        it.copy(user = user, apartment = apartment, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
