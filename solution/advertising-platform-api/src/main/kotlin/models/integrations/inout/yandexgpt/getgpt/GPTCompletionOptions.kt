package ru.cwshbr.models.integrations.inout.yandexgpt.getgpt

import kotlinx.serialization.Serializable

@Serializable
data class GPTCompletionOptions (
    val stream: Boolean = false,
    val temperature: Float = 0.6f,
    val maxToken: String = "1000",
    val reasoningOptions: ReasoningOptions = ReasoningOptions()
)

@Serializable
data class ReasoningOptions(
    val mode: String = "DISABLED"
)