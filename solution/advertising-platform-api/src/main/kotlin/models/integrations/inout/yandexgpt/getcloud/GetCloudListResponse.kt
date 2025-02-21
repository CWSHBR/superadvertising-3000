package ru.cwshbr.models.integrations.inout.yandexgpt.getcloud

import kotlinx.serialization.Serializable

@Serializable
data class GetCloudListResponse(
    val clouds: List<GetCloudIdResponse>
)
