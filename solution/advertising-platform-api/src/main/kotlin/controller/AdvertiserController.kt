package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.cwshbr.database.crud.AdvertisersCRUD
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.models.inout.advertisers.AdvertisersRequestResponseModel
import ru.cwshbr.models.inout.advertisers.UpdateMLScoreModel
import java.util.*

class AdvertiserController(val call: ApplicationCall) {
    suspend fun createAdvertisers() {
        val r = call.receive<List<AdvertisersRequestResponseModel>>()

        r.forEachIndexed { index, advertiserio ->
            if (!advertiserio.validate()) {
                call.respond(HttpStatusCode.BadRequest,
                    ErrorResponse("bad request data in advertiser at $index"))
                return
            }
        }

        val advertisers = r.map { it.toAdvertiserModel() }

        val (success, reason) = AdvertisersCRUD.createOrUpdateList(advertisers)

        if (!success) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(reason.toString())) //TODO do not show reason
            return
        }

        call.respond(HttpStatusCode.Created, r) // TODO GET ONLY UNIQUE LAST CHANGES
    }

    suspend fun getAdvertiser() {
        val id = try {
            UUID.fromString(call.parameters["advertiserId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad id"))
            return
        }

        val advertiser = AdvertisersCRUD.readById(id)

        if (advertiser == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Advertiser not found"))
            return
        }

        call.respond(HttpStatusCode.OK, advertiser.toAdvertiserResponseModel())
    }

    suspend fun updateMlScore() {
        val r = call.receive<UpdateMLScoreModel>()

        if (!r.validate()) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad data"))
            return
        }

        val mlscore = r.convert()

        val (success, reason) = AdvertisersCRUD.updateMlScore(
            mlscore.first,
            mlscore.second,
            mlscore.third
        )

        if (!success) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Something wasn't found right"))
            return
        }

        call.respond(HttpStatusCode.OK)
    }
}