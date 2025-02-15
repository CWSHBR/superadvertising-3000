package ru.cwshbr.models.inout.stats

import kotlinx.serialization.Serializable

@Serializable
data class StatsResponseModel(
    val impressions_count: Int,
    val click_count: Int,
    val conversion: Float,
    val spent_impressions: Float,
    val spent_clicks: Float,
    val spent_total: Float,
)
