package ru.cwshbr.models.inout.advertiser

import kotlinx.serialization.Serializable

@Serializable
data class AdvertisersResponseModel(
     val advertiser_id: String,
     val name: String
)
