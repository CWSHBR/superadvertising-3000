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
) {
    fun toDaily(date: Int) =
        DailyStatsResponseModel(
            impressions_count,
            click_count,
            conversion,
            spent_impressions,
            spent_clicks,
            spent_total,
            date
        )
}
