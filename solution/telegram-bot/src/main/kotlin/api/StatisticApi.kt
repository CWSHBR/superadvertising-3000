package ru.cwshbr.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import ru.cwshbr.models.inout.stats.StatsResponseModel
import ru.cwshbr.plugins.client
import ru.cwshbr.utils.BASE_URL

object StatisticApi {
    suspend fun getStatsByAdvertiser(advertiserId: String): StatsResponseModel? {
        val res = client.get("$BASE_URL/stats/advertisers/${advertiserId}/campaigns")
        if (res.status.value != 200) return null
        return res.body<StatsResponseModel>()
    }

    suspend fun getStatsByCampaign(campaignId: String): StatsResponseModel? {
        val res = client.get("$BASE_URL/stats/campaigns/${campaignId}")
        if (res.status.value != 200) return null
        return res.body<StatsResponseModel>()
    }
}