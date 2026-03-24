package com.exe202.nova.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.repository.GeminiChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
data class AiChatMessage(
    val text: String,
    val fromResident: Boolean,
    val timestampMillis: Long = System.currentTimeMillis()
)
data class ResidentAiChatUiState(
    val messages: List<AiChatMessage> = listOf(
        AiChatMessage(
            text = "Xin chao, toi la AI ho tro Nova Home. Ban can giup gi ve phi dich vu, thong bao hay quy trinh?",
            fromResident = false
        )
    ),
    val input: String = "",
    val isSending: Boolean = false,
    val error: String? = null
)
@HiltViewModel
class ResidentAiChatViewModel @Inject constructor(
    private val geminiChatRepository: GeminiChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResidentAiChatUiState())
    val uiState: StateFlow<ResidentAiChatUiState> = _uiState
    fun onInputChange(value: String) {
        _uiState.update { it.copy(input = value, error = null) }
    }
    fun sendMessage() {
        val message = _uiState.value.input.trim()
        if (message.isBlank() || _uiState.value.isSending) return
        val userMessage = AiChatMessage(text = message, fromResident = true)
        _uiState.update {
            it.copy(
                input = "",
                isSending = true,
                messages = it.messages + userMessage,
                error = null
            )
        }
        viewModelScope.launch {
            try {
                val history = _uiState.value.messages.map { chatMessage ->
                    if (chatMessage.fromResident) "Resident" to chatMessage.text else "AI" to chatMessage.text
                }
                val answer = geminiChatRepository.ask(question = message, history = history)
                _uiState.update {
                    it.copy(
                        isSending = false,
                        messages = it.messages + AiChatMessage(text = answer, fromResident = false)
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSending = false,
                        error = "Khong gui duoc cho AI. Vui long thu lai."
                    )
                }
            }
        }
    }
}
