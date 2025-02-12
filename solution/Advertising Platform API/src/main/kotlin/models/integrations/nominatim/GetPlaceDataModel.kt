package ru.cwshbr.models.integrations.nominatim

import kotlinx.serialization.Serializable

@Serializable
data class GetPlaceDataModel(
    val lat: Double,
    val lon: Double,
    val boundingbox: List<Double>
)
