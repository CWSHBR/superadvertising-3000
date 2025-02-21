package ru.cwshbr.models.integrations.inout.yandexgpt.getgpt

import kotlinx.serialization.Serializable

@Serializable
data class AlternativeModel(
    val message: MessageModel,
    val status: String
)
