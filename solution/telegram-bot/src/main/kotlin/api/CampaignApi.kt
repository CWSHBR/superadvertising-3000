package ru.cwshbr.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
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

    suspend fun getImage(advertiserId: UUID, campaignId: UUID, notCached: Boolean = false): ByteArray? {
        val cachedImage = Caching.getImage(campaignId)

        if (cachedImage != null && !notCached) {
            return cachedImage
        }

        val response = client.get("$BASE_URL/advertisers/$advertiserId/campaigns/$campaignId/image")

        if (response.status.value == 200) {
            val r = response.bodyAsBytes()

            Caching.cacheImage(campaignId, r)

            return r
        }

        return null
    }

    suspend fun setImage(advertiserId: UUID, campaignId: UUID, image: ByteArray): Pair<Boolean, String?> {
        val response = client.post("$BASE_URL/advertisers/$advertiserId/campaigns/$campaignId/image"){
            contentType(ContentType.Image.JPEG)
            setBody(image)
        }

        return when (response.status.value) {
            200 -> Pair(true, null)
            413 -> Pair(false, "Фотография превышает лимит веса в 5 МБ.")
            else -> Pair(false, "Неизвестная ошибка.")
        }
    }


}