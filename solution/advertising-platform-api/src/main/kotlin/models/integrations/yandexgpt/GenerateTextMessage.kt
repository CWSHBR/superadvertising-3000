package ru.cwshbr.models.integrations.yandexgpt

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTextMessage(
    val title: String,
    val adv_name: String,
    val adv_id: String,
    val campaign_id: String,
)
