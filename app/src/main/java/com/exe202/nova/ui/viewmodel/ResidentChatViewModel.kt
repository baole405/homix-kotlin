package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.ChatMessage
import com.exe202.nova.data.repository.AuthRepository
import com.exe202.nova.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResidentChatUiState(
    val residentId: String = "",
    val residentName: String = "",
    val threadId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ResidentChatViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResidentChatUiState())
    val uiState: StateFlow<ResidentChatUiState> = _uiState

    private var messagesJob: Job? = null

    init {
        loadCurrentResident()
    }

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun sendMessage() {
        val state = _uiState.value
        val message = state.input.trim()
        if (message.isBlank() || state.residentId.isBlank()) return

        viewModelScope.launch {
            try {
                chatRepository.sendResidentMessage(
                    residentId = state.residentId,
                    residentName = state.residentName.ifBlank { "Resident ${state.residentId}" },
                    message = message
                )
                _uiState.update { it.copy(input = "", error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Khong gui duoc tin nhan. Vui long thu lai.") }
            }
        }
    }

    private fun loadCurrentResident() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val user = authRepository.getMe()
                val residentId = user.id.toString()
                val residentName = user.fullName?.takeIf { it.isNotBlank() } ?: user.username
                val threadId = chatRepository.buildResidentThreadId(residentId)

                _uiState.update {
                    it.copy(
                        residentId = residentId,
                        residentName = residentName,
                        threadId = threadId,
                        isLoading = false
                    )
                }
                observeMessages(threadId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Khong tai duoc thong tin chat."
                    )
                }
            }
        }
    }

    private fun observeMessages(threadId: String) {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            chatRepository.observeMessages(threadId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }
}

