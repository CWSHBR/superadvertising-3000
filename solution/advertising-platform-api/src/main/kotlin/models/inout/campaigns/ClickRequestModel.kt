package ru.cwshbr.models.inout.campaigns

import kotlinx.serialization.Serializable

@Serializable
data class ClickRequestModel(
    val client_id: String
)
