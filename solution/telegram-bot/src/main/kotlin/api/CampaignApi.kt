package ru.cwshbr.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.inout.campaign.GetCampaignResponseModel
import ru.cwshbr.plugins.client
import ru.cwshbr.states.Caching
import ru.cwshbr.utils.BASE_URL
import java.util.*

object CampaignApi {
    suspend fun getCampaignsList(advertiserId: UUID, page: Int = 0, size: Int = 5): List<CampaignModel> {
        val response = client.get("$BASE_URL/advertisers/$advertiserId/campaigns") {
            parameter("page", page)
            parameter("size", size)
        }

        if (response.status.value == 200) {
            val r = response.body<List<GetCampaignResponseModel>>()
            val campaignList = r.map { it.toCampaignModel() }

            Caching.writeNewCampaigns(campaignList)

            return campaignList
        }

        return listOf()
    }

    suspend fun getCampaign(advertiserId: UUID, campaignId: UUID, notCached: Boolean = false): CampaignModel? {
        val cachedCampaign = Caching.getCampaign(campaignId)

        if (cachedCampaign != null && !notCached) {
            println("GOT CACHED: ${cachedCampaign.adText}")
            return cachedCampaign
        }

        val response = client.get("$BASE_URL/advertisers/$advertiserId/campaigns/$campaignId")

        if (response.status.value == 200) {
            val r = response.body<GetCampaignResponseModel>()
            val campaign = r.toCampaignModel()

            Caching.writeNewCampaigns(listOf(campaign))

            println("GOT NEW: ${campaign.adText}")
            return campaign
        }

        println("NOT FOUND")
        return null
    }


}