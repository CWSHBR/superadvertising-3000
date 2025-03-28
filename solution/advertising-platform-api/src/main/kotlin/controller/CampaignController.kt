package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.jsonObject
import ru.cwshbr.database.crud.AdvertisersCRUD
import ru.cwshbr.database.crud.CampaignsCRUD
import ru.cwshbr.database.crud.ClientsCRUD
import ru.cwshbr.database.crud.StatisticsCRUD
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.models.inout.campaigns.ClickRequestModel
import ru.cwshbr.models.inout.campaigns.CreateCampaignRequestModel
import ru.cwshbr.models.inout.campaigns.UpdateCampaignRequestModel
import ru.cwshbr.plugins.JsonFormat
import ru.cwshbr.utils.Moderation
import java.util.*

class CampaignController(val call: ApplicationCall) {
    suspend fun getCampaignById(){
        val advertiserId = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad advertiserId"))
            return
        }

        val campaignId = try {
            UUID.fromString(call.parameters["campaignId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad campaignId"))
            return
        }

        val campaign = CampaignsCRUD.read(campaignId)

        if (campaign == null || campaign.advertiserId != advertiserId) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign with this advertiser not found"))
            return
        }

        return call.respond(HttpStatusCode.OK, campaign.toResponseModel())
    }

    suspend fun updateCampaign(){
        val advertiserId = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad advertiserId"))
            return
        }

        val campaignId = try {
            UUID.fromString(call.parameters["campaignId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad campaignId"))
            return
        }

        val campaign = CampaignsCRUD.read(campaignId)

        if (campaign == null || campaign.advertiserId != advertiserId) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign with this advertiser not found"))
            return
        }

        val rText = call.receiveText()
        val r = JsonFormat.decodeFromString<UpdateCampaignRequestModel>(rText)
        val rjson = JsonFormat.parseToJsonElement(rText).jsonObject

        val updatedModel = r.toUpdatedModel(campaign, rjson)

        if (updatedModel == null) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad update data"))
            return
        }

        if (!Moderation.checkText(updatedModel.adText)){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("В тексте содержатся запрещенные слова."))
            return
        }

        val (success, reason) = CampaignsCRUD.update(updatedModel)

        if (!success) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Unknown campaign error."))
            return
        }

        call.respond(HttpStatusCode.OK, updatedModel.toResponseModel())


    }

    suspend fun getListOfCampaigns(){
        val advertiserId = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad advertiserId"))
            return
        }

        if (!AdvertisersCRUD.checkExists(advertiserId)){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Advertiser not found"))
            return
        }

        val (size, page) = try {
            Pair(call.parameters["size"]!!.toInt(), call.parameters["page"]!!.toInt())
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad pagination"))
            return
        }

        val campaigns = CampaignsCRUD.readByAdvertiserId(advertiserId, size, page)

        call.respond(HttpStatusCode.OK, campaigns.map { it.toResponseModel() })
    }

    suspend fun createCampaign(){
        val advertiserId = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad advertiserId"))
            return
        }

        if (!AdvertisersCRUD.checkExists(advertiserId)){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Advertiser not found"))
            return
        }

        val r = call.receive<CreateCampaignRequestModel>()

        if (!r.validate()){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request data"))
            return
        }

        if (!Moderation.checkText(r.ad_text)){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("В тексте содержатся запрещенные слова."))
            return
        }

        val campaignId = UUID.randomUUID()
        val campaign = r.toCampaignModel(campaignId, advertiserId)

        val (success, reason) = CampaignsCRUD.create(campaign)

        if (!success){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Unknown campaign error."))
            return
        }

        call.respond(HttpStatusCode.Created, campaign.toResponseModel())
    }

    suspend fun deleteCampaign(){
        val advertiserId = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad advertiserId"))
            return
        }

        val campaignId = try {
            UUID.fromString(call.parameters["campaignId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad campaignId"))
            return
        }

        val campaign = CampaignsCRUD.read(campaignId)

        if (campaign == null || campaign.advertiserId != advertiserId) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign with this advertiser not found"))
            return
        }

        CampaignsCRUD.delete(campaignId)

        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun getAd(){
        val clientId = try {
            UUID.fromString(call.parameters["client_id"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad client_id"))
            return
        }

        val client = ClientsCRUD.read(clientId)

        if (client == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Client not found"))
            return
        }

        val ads = CampaignsCRUD.getAd(clientId)

        if (ads.isEmpty()) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Ad not found"))
            return
        }

        val ad = ads.first()

        StatisticsCRUD.createImpression(ad, clientId)

        call.respond(HttpStatusCode.OK, ad.toClientAdModel())
    }

    suspend fun clickAd(){
        val adId = try {
            UUID.fromString(call.parameters["adId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad adId"))
            return
        }

        val clientId = try {
            UUID.fromString(call.receive<ClickRequestModel>().client_id)
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad client_id"))
            return
        }

        if(!StatisticsCRUD.impressionExists(adId, clientId)){
            call.respond(HttpStatusCode.Forbidden, ErrorResponse("Не было просмотра рекламы"))
            return
        }

        val campaign = CampaignsCRUD.read(adId)!!

        val stats = StatisticsCRUD.countAllClicks(campaign.id)

        if (stats != null && stats.count >= campaign.clicksLimit){
            call.respond(HttpStatusCode.TooManyRequests, ErrorResponse("You cannot click above the limit"))
            return
        }

        StatisticsCRUD.createClick(campaign, clientId)

        call.respond(HttpStatusCode.NoContent)
    }
}