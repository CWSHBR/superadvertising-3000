package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.cwshbr.database.crud.AdvertisersCRUD
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.models.inout.advertisers.AdvertisersRequestResponseModel
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
            call.respond(HttpStatusCode.NotFound, ErrorResponse(reason.toString()))
            return
        }

        call.respond(HttpStatusCode.OK, r)
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
}