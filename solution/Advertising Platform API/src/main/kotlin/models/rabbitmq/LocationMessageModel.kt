package ru.cwshbr.models.rabbitmq

import kotlinx.serialization.Serializable

@Serializable
data class LocationMessageModel (
    val id: String,
    val location: String,
    val isForCampaign: Boolean = false
)