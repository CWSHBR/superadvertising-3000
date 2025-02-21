package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ru.cwshbr.database.crud.AdvertisersCRUD
import ru.cwshbr.database.crud.CampaignsCRUD
import ru.cwshbr.integrations.yandexgpt.GptQueue
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.models.integrations.yandexgpt.GenerateTextMessage
import java.util.*

class TextGenerationController(val call: ApplicationCall) {
    suspend fun generateText() {
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
        val advertiser = AdvertisersCRUD.readById(advertiserId)

        if (campaign == null || advertiser == null || campaign.advertiserId != advertiserId) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign with this advertiser not found"))
            return
        }

        val result = GptQueue.addToQueue(
            GenerateTextMessage(
                campaign.adTitle,
                advertiser.name,
                advertiserId.toString(),
                campaignId.toString()
            )
        )

        if (!result){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Something went wrong"))
            return
        }

        call.respond(HttpStatusCode.OK, mapOf("status" to
                "ok. wait and check result at /advertisers/${advertiserId}/campaigns/${campaignId}"))

    }
}