package ru.cwshbr.models.inout.campaign

import kotlinx.serialization.Serializable

@Serializable
data class TargetResponseModel(
    val gender: String? = null,
    val age_from: Int? = null,
    val age_to: Int? = null,
    val location: String? = null
)
