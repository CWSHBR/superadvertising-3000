package ru.cwshbr.models.inout.campaigns

import kotlinx.serialization.Serializable

@Serializable
data class GetAdResponseModel(
    val ad_id: String,
    val ad_title: String,
    val ad_description: String,
    val advertiser_id: String
)
