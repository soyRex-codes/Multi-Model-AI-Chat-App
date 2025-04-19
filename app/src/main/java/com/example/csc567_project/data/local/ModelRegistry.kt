package com.example.csc567_project.data.local

object ModelRegistry {
    val supportedModels = mapOf(
        "GPT-4o (OpenAI)" to "gpt-4o",
        "GPT-3.5 Turbo" to "gpt-3.5-turbo",
        "Claude 3 Haiku" to "claude-3-haiku-20240307",
        "Claude 3 Opus" to "claude-3-opus-20240229",
        "Gemini 1.5 Pro" to "gemini-1.5-pro",
        "Gemini 2.5 Pro (Preview)" to "google/gemini-2.5-pro-preview",
        "LLaMA 3 8B (Meta)" to "meta-llama/Llama-3-8b-chat-hf",
        "Mixtral 8x7B" to "mistralai/Mixtral-8x7B-Instruct-v0.1",
        "DeepSeek Chat V3" to "deepseek/deepseek-chat-v3-0324",
        "Qwen 2.5 7B Turbo" to "Qwen/Qwen2.5-7B-Instruct-Turbo"
    )

    fun getModelNames(): List<String> {
        return supportedModels.keys.toList()
    }

    fun getModelId(displayName: String): String {
        return supportedModels[displayName] ?: "gpt-4o"
    }
}