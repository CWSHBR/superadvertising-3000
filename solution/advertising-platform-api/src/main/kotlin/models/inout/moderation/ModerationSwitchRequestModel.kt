package ru.cwshbr.models.inout.moderation

import kotlinx.serialization.Serializable

@Serializable
data class ModerationSwitchRequestModel(
    val turn_on: Boolean,
)
