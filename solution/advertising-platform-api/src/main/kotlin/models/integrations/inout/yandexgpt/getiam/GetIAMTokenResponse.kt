package ru.cwshbr.models.integrations.inout.yandexgpt.getiam

import kotlinx.serialization.Serializable

@Serializable
data class GetIAMTokenResponse(
    val iamToken: String
)
