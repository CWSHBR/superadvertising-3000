package ru.cwshbr.models.inout

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)
