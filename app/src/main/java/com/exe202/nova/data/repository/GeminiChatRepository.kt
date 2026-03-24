package com.exe202.nova.data.repository

import android.content.Context
import com.exe202.nova.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiChatRepository @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val projectContext: String by lazy {
        runCatching {
            appContext.assets.open(CONTEXT_ASSET_PATH).bufferedReader().use { it.readText() }
        }.getOrElse { "" }
    }

    suspend fun ask(question: String, history: List<Pair<String, String>>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.ifBlank { GEMINI_API_KEY_FALLBACK }
        if (apiKey.isBlank()) {
            return@withContext "Chưa cấu hình GEMINI_API_KEY."
        }

        val prompt = buildPrompt(question, history)
        val url = "$GEMINI_ENDPOINT?key=$apiKey"

        val payload = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                }
            ))
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.5)
                put("maxOutputTokens", 700)
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        val response = client.newCall(request).execute()
        val rawBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            val message = runCatching {
                JSONObject(rawBody).optJSONObject("error")?.optString("message")
            }.getOrNull().orEmpty()
            return@withContext if (message.isNotBlank()) {
                "AI đang bận, thử lại sau: $message"
            } else {
                "AI đang bận, thử lại sau."
            }
        }

        val text = runCatching {
            val json = JSONObject(rawBody)
            json.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
                .orEmpty()
        }.getOrDefault("")

        if (text.isBlank()) "AI chưa trả lời được lúc này." else text.trim()
    }

    private fun buildPrompt(question: String, history: List<Pair<String, String>>): String {
        val historyBlock = history.takeLast(8).joinToString("\n") { (role, message) ->
            "$role: $message"
        }

        return buildString {
            appendLine("Ban là trợ lý căn trọ chung cư Nova Home. Trả lời ngắn gọn, thân thiện, dễ hiểu, bằng tiếng Việt, không cần in đậm in nghiêng gì hết.")
            appendLine("Dùng những thông tin sau để tư vấn, về việc giá cả hay những thông tin khác liên quan đến trọ, hãy tự gen với giả cả và tình huống hợp lí nhé :")
            appendLine(projectContext.ifBlank { "(Khong co context noi bo)" })
            appendLine()
            appendLine("Đoạn hội thoại gần đây:")
            appendLine(historyBlock.ifBlank { "(không có)" })
            appendLine()
            appendLine("Câu hỏi mới của cư dân:")
            appendLine(question)
        }
    }

    private companion object {
        const val GEMINI_API_KEY_FALLBACK = "AIzaSyBQqtvoe53CB4r82mf_kT5DP1OjzwQA6Ik"
        const val GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
        const val CONTEXT_ASSET_PATH = "context.txt"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}



