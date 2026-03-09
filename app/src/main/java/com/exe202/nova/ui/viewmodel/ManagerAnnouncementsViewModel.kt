package com.exe202.nova.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exe202.nova.data.model.Announcement
import com.exe202.nova.data.model.AnnouncementCategory
import com.exe202.nova.data.model.AnnouncementPriority
import com.exe202.nova.data.repository.AnnouncementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class ManagerAnnouncementsUiState(
    val announcements: List<Announcement> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class CreateAnnouncementUiState(
    val title: String = "",
    val content: String = "",
    val category: AnnouncementCategory = AnnouncementCategory.GENERAL,
    val priority: AnnouncementPriority = AnnouncementPriority.NORMAL,
    val pinned: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ManagerAnnouncementsViewModel @Inject constructor(
    private val repository: AnnouncementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerAnnouncementsUiState())
    val uiState: StateFlow<ManagerAnnouncementsUiState> = _uiState

    private val _createState = MutableStateFlow(CreateAnnouncementUiState())
    val createState: StateFlow<CreateAnnouncementUiState> = _createState

    init {
        loadAnnouncements()
    }

    fun loadAnnouncements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val list = repository.getAllAnnouncements()
                _uiState.update { it.copy(announcements = list, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteAnnouncement(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteAnnouncement(id)
                loadAnnouncements()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateTitle(value: String) = _createState.update { it.copy(title = value) }
    fun updateContent(value: String) = _createState.update { it.copy(content = value) }
    fun updateCategory(value: AnnouncementCategory) = _createState.update { it.copy(category = value) }
    fun updatePriority(value: AnnouncementPriority) = _createState.update { it.copy(priority = value) }
    fun updatePinned(value: Boolean) = _createState.update { it.copy(pinned = value) }

    fun submitAnnouncement() {
        val state = _createState.value
        if (state.title.isBlank() || state.content.isBlank()) {
            _createState.update { it.copy(error = "Vui lòng điền đầy đủ tiêu đề và nội dung") }
            return
        }
        viewModelScope.launch {
            _createState.update { it.copy(isSubmitting = true, error = null) }
            try {
                val announcement = Announcement(
                    id = UUID.randomUUID().toString(),
                    title = state.title,
                    content = state.content,
                    author = "Ban Quản Lý",
                    category = state.category,
                    priority = state.priority,
                    createdAt = LocalDate.now().toString(),
                    imageUrl = null,
                    pinned = state.pinned
                )
                repository.createAnnouncement(announcement)
                _createState.update { it.copy(isSubmitting = false, submitted = true) }
                loadAnnouncements()
            } catch (e: Exception) {
                _createState.update { it.copy(isSubmitting = false, error = e.message) }
            }
        }
    }

    fun resetCreateForm() {
        _createState.value = CreateAnnouncementUiState()
    }
}
