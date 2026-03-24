package com.exe202.nova.data.repository

import com.exe202.nova.data.model.ChatMessage
import com.exe202.nova.data.model.ChatThread
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class ChatRepository @Inject constructor() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun buildResidentThreadId(residentId: String): String = "resident_$residentId"

    fun observeManagerThreads(): Flow<List<ChatThread>> = callbackFlow {
        val registration = firestore.collection(CHAT_THREADS_COLLECTION)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val threads = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    val residentId = doc.getString("residentId") ?: return@mapNotNull null
                    ChatThread(
                        id = doc.id,
                        residentId = residentId,
                        residentName = doc.getString("residentName").orEmpty().ifBlank { "Resident $residentId" },
                        lastMessage = doc.getString("lastMessage").orEmpty(),
                        updatedAtMillis = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L
                    )
                }
                trySend(threads)
            }

        awaitClose { registration.remove() }
    }

    fun observeMessages(threadId: String): Flow<List<ChatMessage>> = callbackFlow {
        val registration = firestore.collection(CHAT_THREADS_COLLECTION)
            .document(threadId)
            .collection(MESSAGES_COLLECTION)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents.orEmpty().map { doc ->
                    ChatMessage(
                        id = doc.id,
                        senderId = doc.getString("senderId").orEmpty(),
                        senderRole = doc.getString("senderRole").orEmpty(),
                        senderName = doc.getString("senderName").orEmpty(),
                        text = doc.getString("text").orEmpty(),
                        createdAtMillis = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                }
                trySend(messages)
            }

        awaitClose { registration.remove() }
    }

    suspend fun sendResidentMessage(
        residentId: String,
        residentName: String,
        message: String
    ) {
        val threadId = buildResidentThreadId(residentId)
        sendMessage(
            threadId = threadId,
            residentId = residentId,
            residentName = residentName,
            senderId = residentId,
            senderRole = "resident",
            senderName = residentName,
            message = message
        )
    }

    suspend fun sendManagerMessage(
        threadId: String,
        residentId: String,
        residentName: String,
        managerId: String,
        managerName: String,
        message: String
    ) {
        sendMessage(
            threadId = threadId,
            residentId = residentId,
            residentName = residentName,
            senderId = managerId,
            senderRole = "manager",
            senderName = managerName,
            message = message
        )
    }

    private suspend fun sendMessage(
        threadId: String,
        residentId: String,
        residentName: String,
        senderId: String,
        senderRole: String,
        senderName: String,
        message: String
    ) {
        val now = Timestamp.now()
        val threadRef = firestore.collection(CHAT_THREADS_COLLECTION).document(threadId)
        val messageRef = threadRef.collection(MESSAGES_COLLECTION).document()

        val threadData = mapOf(
            "residentId" to residentId,
            "residentName" to residentName,
            "lastMessage" to message,
            "updatedAt" to now
        )

        val messageData = mapOf(
            "senderId" to senderId,
            "senderRole" to senderRole,
            "senderName" to senderName,
            "text" to message,
            "createdAt" to now
        )

        val batch = firestore.batch()
        batch.set(threadRef, threadData, SetOptions.merge())
        batch.set(messageRef, messageData)
        batch.commit().awaitResult()
    }

    private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitResult(): T {
        return suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result ->
                if (continuation.isActive) continuation.resume(result)
            }
            addOnFailureListener { error ->
                if (continuation.isActive) continuation.resumeWithException(error)
            }
        }
    }

    private companion object {
        const val CHAT_THREADS_COLLECTION = "chat_threads"
        const val MESSAGES_COLLECTION = "messages"
    }
}

