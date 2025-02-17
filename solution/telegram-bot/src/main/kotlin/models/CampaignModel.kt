package ru.cwshbr.models


import java.util.UUID

data class CampaignModel(
    val id: UUID,
    val advertiserId: UUID,
    val impressionsLimit: Int,
    val clicksLimit: Int,
    val costPerImpression: Float,
    val costPerClick: Float,
    val adTitle: String,
    val adText: String,
    val startDate: Int,
    val endDate: Int,
    val target: CampaignTarget?
)
