package com.exe202.nova.data.model

data class ChatThread(
    val id: String,
    val residentId: String,
    val residentName: String,
    val lastMessage: String,
    val updatedAtMillis: Long
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderRole: String,
    val senderName: String,
    val text: String,
    val createdAtMillis: Long
)

