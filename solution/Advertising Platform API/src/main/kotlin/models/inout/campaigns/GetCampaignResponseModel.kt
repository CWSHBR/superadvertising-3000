package ru.cwshbr.models.inout.campaigns

import kotlinx.serialization.Serializable

@Serializable
data class GetCampaignResponseModel(
    val campaign_id: String,
    val advertiser_id: String,
    val impressions_limit: Int,
    val clicks_limit: Int,
    val cost_per_impression: Float,
    val cost_per_click: Float,
    val ad_title: String,
    val ad_text: String,
    val start_date: Int,
    val end_date: Int,
    val target: TargetResponseModel
)
