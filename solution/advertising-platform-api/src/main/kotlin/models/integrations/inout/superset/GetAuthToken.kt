package ru.cwshbr.models.integrations.inout.superset

import kotlinx.serialization.Serializable

@Serializable
data class GetAuthToken(
    val access_token: String
)
