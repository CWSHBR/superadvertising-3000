package ru.cwshbr.models.inout.time

import kotlinx.serialization.Serializable

@Serializable
data class CurrentTimeModel(
    val current_date: Int
)
