package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ru.cwshbr.database.crud.AdvertisersCRUD
import ru.cwshbr.database.crud.CampaignsCRUD
import ru.cwshbr.database.crud.StatisticsCRUD
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.utils.StatsTransforming
import java.util.*

class StatisticsController(val call: ApplicationCall) {
    suspend fun getCampaignStats(){
        val campaignId = try {
            UUID.fromString(call.parameters["campaignId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad campaignId"))
            return
        }

        if (CampaignsCRUD.read(campaignId) == null){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign not found"))
            return
        }

        val stats = StatsTransforming.toStatsResponseModel(
            StatisticsCRUD.countAllImpressions(campaignId),
            StatisticsCRUD.countAllClicks(campaignId)
        )

        call.respond(HttpStatusCode.OK, stats)
    }

    suspend fun getCampaignDailyStats(){
        val campaignId = try {
            UUID.fromString(call.parameters["campaignId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad campaignId"))
            return
        }

        if (CampaignsCRUD.read(campaignId) == null){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign not found"))
            return
        }

        val stats = StatsTransforming.toDailyStatsList(
            StatisticsCRUD.countDailyImpressions(campaignId),
            StatisticsCRUD.countDailyClicks(campaignId)
        )

        call.respond(HttpStatusCode.OK, stats)
    }

    suspend fun getAdvertiserStats(){
        val advertiserId = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad advertiserId"))
            return
        }

        if (!AdvertisersCRUD.checkExists(advertiserId)){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign not found"))
            return
        }

        val stats = StatsTransforming.toStatsResponseModel(
            StatisticsCRUD.countAllImpressionsByAdvertiser(advertiserId),
            StatisticsCRUD.countAllClicksByAdvertiser(advertiserId)
        )

        call.respond(HttpStatusCode.OK, stats)
    }


    suspend fun getAdvertiserDailyStats(){
        val advertiserId = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad advertiserId"))
            return
        }

        if (!AdvertisersCRUD.checkExists(advertiserId)){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Campaign not found"))
            return
        }

        val stats = StatsTransforming.toDailyStatsList(
            StatisticsCRUD.countDailyImpressionsByAdvertiser(advertiserId),
            StatisticsCRUD.countDailyClicksByAdvertiser(advertiserId)
        )

        call.respond(HttpStatusCode.OK, stats)
    }
}