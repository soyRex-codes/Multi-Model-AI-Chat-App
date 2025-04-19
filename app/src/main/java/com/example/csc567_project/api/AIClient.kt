package com.example.csc567_project.api

import android.util.Log
import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.example.csc567_project.data.local.ChatMessageEntity

class AIClient(
    apiKey: String,
    private var model: String = "gpt-4o",
    baseUrl: String = "https://api.aimlapi.com/v1"
) {
    private val ai: OpenAI = OpenAI(
        OpenAIConfig(
            token = apiKey,
            host = OpenAIHost(baseUrl = baseUrl)
        )
    )

    private var history: List<ChatMessageEntity> = mutableListOf()

    fun getModel(): String {
        return this.model
    }

    fun setHistory(newHistory: List<ChatMessageEntity>) {
        history = newHistory
    }

    suspend fun getAvailableModels(): List<Model> {
        return this.ai.models()
    }

    suspend fun sendChat(message: String): ChatCompletion {
        val chatHistory = this.history.map {
            ChatMessage(
                role = if (it.isAI) ChatRole.Assistant else ChatRole.User,
                content = it.message
            )
        }.toMutableList()

        chatHistory.add(
            ChatMessage(
                role = ChatRole.User,
                content = message
            )
        )

        val request = ChatCompletionRequest(
            model = ModelId(this.getModel()),
            messages = chatHistory
        )

        Log.d("AIClient", "Sending to model: $model")
        Log.d("AIClient", "Chat history size: ${chatHistory.size}")

        return try {
            val result = this.ai.chatCompletion(request)
            Log.d("AIClient", "Response: ${result.choices.firstOrNull()?.message?.content}")
            result
        } catch (e: Exception) {
            Log.e("AIClient", "Error during chatCompletion: ${e.message}", e)
            throw e
        }
    }
}