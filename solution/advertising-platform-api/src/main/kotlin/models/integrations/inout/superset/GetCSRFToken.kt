package ru.cwshbr.models.integrations.inout.superset

import kotlinx.serialization.Serializable

@Serializable
data class GetCSRFToken(
    val result: String
)
