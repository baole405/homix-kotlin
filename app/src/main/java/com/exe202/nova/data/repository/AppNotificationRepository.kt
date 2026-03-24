package com.exe202.nova.data.repository

import com.exe202.nova.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

private data class AppNotificationEntry(
    val key: String,
    val title: String,
    val content: String,
    val createdAt: String,
    val isRead: Boolean,
    val type: String?
)

@Singleton
class AppNotificationRepository @Inject constructor() {
    private val _entries = MutableStateFlow<List<AppNotificationEntry>>(emptyList())
    private val _localNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val localNotifications: StateFlow<List<Notification>> = _localNotifications.asStateFlow()

    fun upsert(
        key: String,
        title: String,
        content: String,
        type: String? = null,
        createdAt: String = LocalDateTime.now().toString()
    ) {
        val current = _entries.value.toMutableList()
        val index = current.indexOfFirst { it.key == key }
        val next = if (index >= 0) {
            current[index].copy(
                title = title,
                content = content,
                createdAt = createdAt,
                type = type ?: current[index].type
            )
        } else {
            AppNotificationEntry(
                key = key,
                title = title,
                content = content,
                createdAt = createdAt,
                isRead = false,
                type = type
            )
        }

        if (index >= 0) current[index] = next else current.add(next)
        _entries.value = current.sortedByDescending { it.createdAt }
        syncNotifications()
    }

    fun remove(key: String) {
        _entries.value = _entries.value.filterNot { it.key == key }
        syncNotifications()
    }

    fun markAsReadById(id: Int): Boolean {
        val key = idToKey(id) ?: return false
        markAsReadByKey(key)
        return true
    }

    fun markAsReadByKey(key: String) {
        _entries.value = _entries.value.map { entry ->
            if (entry.key == key) entry.copy(isRead = true) else entry
        }
        syncNotifications()
    }

    fun markAllRead() {
        _entries.value = _entries.value.map { it.copy(isRead = true) }
        syncNotifications()
    }

    fun asNotifications(): List<Notification> = _localNotifications.value

    private fun syncNotifications() {
        _localNotifications.value = _entries.value.map { entry ->
            Notification(
                id = keyToId(entry.key),
                title = entry.title,
                content = entry.content,
                createdAt = entry.createdAt,
                isRead = entry.isRead,
                type = entry.type
            )
        }
    }

    private fun keyToId(key: String): Int {
        return -(abs(key.hashCode()) + 1)
    }

    private fun idToKey(id: Int): String? {
        return _entries.value.firstOrNull { keyToId(it.key) == id }?.key
    }
}
