package ru.cwshbr.models.integrations.inout.yandexgpt.getgpt

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val role: String,
    val text: String
)
