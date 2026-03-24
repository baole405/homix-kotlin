package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.ChatMessage
import com.exe202.nova.data.model.ChatThread
import com.exe202.nova.data.repository.AuthRepository
import com.exe202.nova.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManagerChatUiState(
    val managerId: String = "",
    val managerName: String = "",
    val threads: List<ChatThread> = emptyList(),
    val selectedThreadId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ManagerChatViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerChatUiState())
    val uiState: StateFlow<ManagerChatUiState> = _uiState

    private var messagesJob: Job? = null

    init {
        loadManagerProfile()
        observeThreads()
    }

    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value) }
    }

    fun selectThread(threadId: String) {
        if (_uiState.value.selectedThreadId == threadId) return
        _uiState.update { it.copy(selectedThreadId = threadId, input = "") }
        observeMessages(threadId)
    }

    fun sendMessage() {
        val state = _uiState.value
        val message = state.input.trim()
        val selectedThread = state.threads.firstOrNull { it.id == state.selectedThreadId }
        if (message.isBlank() || selectedThread == null || state.managerId.isBlank()) return

        viewModelScope.launch {
            try {
                chatRepository.sendManagerMessage(
                    threadId = selectedThread.id,
                    residentId = selectedThread.residentId,
                    residentName = selectedThread.residentName,
                    managerId = state.managerId,
                    managerName = state.managerName.ifBlank { "Manager" },
                    message = message
                )
                _uiState.update { it.copy(input = "", error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Khong gui duoc tin nhan. Vui long thu lai.") }
            }
        }
    }

    private fun loadManagerProfile() {
        viewModelScope.launch {
            try {
                val me = authRepository.getMe()
                val managerName = me.fullName?.takeIf { it.isNotBlank() } ?: me.username
                _uiState.update {
                    it.copy(
                        managerId = me.id.toString(),
                        managerName = managerName
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Khong tai duoc thong tin manager.") }
            }
        }
    }

    private fun observeThreads() {
        viewModelScope.launch {
            chatRepository.observeManagerThreads().collect { threads ->
                val currentSelected = _uiState.value.selectedThreadId
                val nextSelected = when {
                    threads.isEmpty() -> null
                    currentSelected != null && threads.any { it.id == currentSelected } -> currentSelected
                    else -> threads.first().id
                }

                _uiState.update {
                    it.copy(
                        threads = threads,
                        selectedThreadId = nextSelected,
                        isLoading = false
                    )
                }

                if (nextSelected == null) {
                    messagesJob?.cancel()
                    _uiState.update { it.copy(messages = emptyList()) }
                } else if (nextSelected != currentSelected) {
                    observeMessages(nextSelected)
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

