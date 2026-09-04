package com.cyberos.app.data

import com.cyberos.app.ui.lang.Lang
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

data class AiChatMessage(val role: String, val content: String)

sealed class AiResult {
    data class Success(val reply: String) : AiResult()
    data class Failure(val userMessage: String) : AiResult()
}

class AiClient {

    private val http = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    suspend fun chat(
        baseUrl: String, apiKey: String, model: String,
        history: List<AiChatMessage>,
        systemPrompt: String = Agents.CHAT_NORMAL
    ): AiResult = withContext(Dispatchers.IO) {
        if (!baseUrl.startsWith("https://")) {
            return@withContext AiResult.Failure(
                Lang.t("Base URL must start with https:// — check Settings.", "عنوان الخدمة لازم يبدأ بـ https:// — راجع الإعدادات.")
            )
        }
        try {
            val messages = JSONArray()
            messages.put(JSONObject().put("role", "system").put("content", systemPrompt))
            history.takeLast(12).forEach { m ->
                messages.put(JSONObject().put("role", m.role).put("content", m.content))
            }
            val body = JSONObject().put("model", model).put("messages", messages).put("temperature", 0.3)

            val req = Request.Builder()
                .url("$baseUrl/chat/completions")
                .header("Authorization", "Bearer $apiKey")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val resp = http.newCall(req).execute()
            try {
                when {
                    resp.code == 401 || resp.code == 403 ->
                        AiResult.Failure(Lang.t("API key rejected (${resp.code}) — check Settings.", "مفتاح API مرفوض (${resp.code}) — راجع الإعدادات."))
                    resp.code == 429 ->
                        AiResult.Failure(Lang.t("Rate limit (429) — wait and retry.", "تجاوزت الحد (429) — استنى وحاول تاني."))
                    !resp.isSuccessful ->
                        AiResult.Failure(Lang.t("Provider returned ${resp.code}.", "الخدمة ردت ${resp.code}."))
                    else -> {
                        val text = resp.body?.string()
                        if (text.isNullOrBlank()) {
                            AiResult.Failure(Lang.t("Empty response.", "رد فاضي."))
                        } else {
                            val reply = try {
                                JSONObject(text).optJSONArray("choices")?.optJSONObject(0)?.optJSONObject("message")?.optString("content")
                            } catch (_: Exception) { null }
                            if (reply.isNullOrBlank()) {
                                AiResult.Failure(Lang.t("Unreadable response.", "رد غير مفهوم."))
                            } else {
                                AiResult.Success(reply.trim())
                            }
                        }
                    }
                }
            } finally { resp.close() }
        } catch (e: UnknownHostException) {
            AiResult.Failure(Lang.t("No internet.", "مفيش إنترنت."))
        } catch (e: IOException) {
            AiResult.Failure(Lang.t("Connection problem.", "مشكلة اتصال."))
        } catch (e: Exception) {
            AiResult.Failure(Lang.t("Unexpected error.", "خطأ غير متوقع."))
        }
    }
}
