package ru.cwshbr.models.integrations.inout.yandexgpt.getgpt

import kotlinx.serialization.Serializable

@Serializable
data class AskGptModel(
    val modelUri: String,
    val completionOptions: GPTCompletionOptions,
    val messages: List<MessageModel>,
)
