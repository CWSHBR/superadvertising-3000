package ru.cwshbr.models.inout.stats

import kotlinx.serialization.Serializable

@Serializable
data class DailyStatsResponseModel(
    val impressions_count: Int,
    val click_count: Int,
    val conversion: Float,
    val spent_impressions: Float,
    val spent_clicks: Float,
    val spent_total: Float,
    val date: Int
)
