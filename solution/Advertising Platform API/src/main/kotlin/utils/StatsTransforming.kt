package ru.cwshbr.utils

import ru.cwshbr.models.StatisticModel
import ru.cwshbr.models.inout.stats.DailyStatsResponseModel
import ru.cwshbr.models.inout.stats.StatsResponseModel

object StatsTransforming {
    fun toStatsResponseModel(impr: StatisticModel?, clck: StatisticModel?): StatsResponseModel {
        val clicks = clck?.count ?: 0
        val impressions = impr?.count ?: 0

        val imCost = impr?.costSum ?: 0f
        val clCost = clck?.costSum ?: 0f

        val conversion = if (impressions == 0) 0f
        else clicks/(impressions.toFloat()) * 100

        return StatsResponseModel(
            impressions,
            clicks,
            conversion,
            imCost,
            clCost,
            imCost + clCost
        )
    }

    fun toDailyStatsList(impr: Map<Int, StatisticModel>, clck: Map<Int, StatisticModel>): List<DailyStatsResponseModel> {
        val dates = impr.keys + clck.keys

        val dailyStats = dates.sorted().map {
            toStatsResponseModel(impr[it], clck[it]).toDaily(it)
        }
        return dailyStats
    }
}