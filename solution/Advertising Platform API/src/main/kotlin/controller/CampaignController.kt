package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.jsonObject
import ru.cwshbr.database.crud.AdvertisersCRUD
import ru.cwshbr.database.crud.CampaignsCRUD
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.models.inout.campaigns.CreateCampaignRequestModel
import ru.cwshbr.models.inout.campaigns.UpdateCampaignRequestModel
import ru.cwshbr.plugins.JsonFormat
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

        val (success, reason) = CampaignsCRUD.update(updatedModel)

        if (!success) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(reason.toString()))
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

        if (!r.validate()){ //todo validate with reason
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request data"))
            return
        }

        val campaignId = UUID.randomUUID()
        val campaign = r.toCampaignModel(campaignId, advertiserId)

        val (success, reason) = CampaignsCRUD.create(campaign)

        if (!success){
            call.respond(HttpStatusCode.BadRequest, reason.toString()) // todo do not request reason
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
}