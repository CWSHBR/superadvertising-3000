package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.cwshbr.database.crud.CampaignsCRUD
import ru.cwshbr.database.crud.ImagesCRUD
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.integrations.objectstorage.ImageLoading
import ru.cwshbr.integrations.objectstorage.ImageLoading.getImageFromS3
import java.util.*

class ImagesController(val call: ApplicationCall) {
    suspend fun setImage() {
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
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Image OR Campaign with this advertiser not found"))
            return
        }

        val type = call.request.headers["Content-Type"]

        if (type != null && type.startsWith("image/")) {
            try {
                val r = call.receiveStream()
                val imageName = UUID.randomUUID().toString()

                val bytes = r.readBytes()
                if (bytes.size > 5242880){
                    call.respond(HttpStatusCode.PayloadTooLarge, ErrorResponse("Image too large. 5MB max"))
                    return
                }

                val lastImage = ImagesCRUD.getImage(campaignId)
                if (lastImage != null) {
                    ImageLoading.deleteImageFromS3(lastImage)
                }

                ImageLoading.saveImageToS3(imageName, bytes)
                ImagesCRUD.setImage(campaignId, imageName)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Something went wrong"))
                return
            }
        }

        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Only images is supported"))
    }

    suspend fun getImage(){
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
        val filename = ImagesCRUD.getImage(campaignId)

        if (campaign == null || campaign.advertiserId != advertiserId || filename == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Image OR Campaign with this advertiser not found"))
            return
        }

        val imageStream = getImageFromS3(filename)

        if (imageStream == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Image does not exist"))
            return
        }

        call.respondOutputStream(ContentType.parse("image/jpeg"), HttpStatusCode.OK){
            write(imageStream.readAllBytes())
            flush()
        }
    }
}