package ru.cwshbr.models.integrations.inout.yandexgpt.getgpt

import kotlinx.serialization.Serializable

@Serializable
data class AskGptResultModel (
    val alternatives: List<AlternativeModel>,
)