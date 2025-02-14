package ru.cwshbr.integrations.nominatim

import io.ktor.client.call.*
import io.ktor.client.request.*
import ru.cwshbr.models.integrations.nominatim.BoundingBox
import ru.cwshbr.models.integrations.nominatim.GetPlaceDataModel
import ru.cwshbr.models.integrations.nominatim.Position
import ru.cwshbr.utils.client

object Nominatim {
    private const val endpoint = "https://nominatim.openstreetmap.org/search"

    private suspend fun sendRequest (placeName: String): GetPlaceDataModel? {
        val places = try {
            val response = client.get(endpoint) {
                parameter("q", placeName)
                parameter("accept-language", "ru")
                parameter("format", "json")
                parameter("limit", 1)
            }
            response.body<List<GetPlaceDataModel>>()
        } catch (e: Exception) {
            null
        }

        if (places.isNullOrEmpty()) return null

        return places.first()
    }

    suspend fun getPlacePosition (placeName: String) =
        sendRequest(placeName).let {
            if (it == null) null else Position(
                it.lat,
                it.lon
            )
        }


    suspend fun getPlaceBoundingBox (placeName: String) =
        sendRequest(placeName).let {
            if (it == null || it.boundingbox.count() != 4) null else
                BoundingBox(
                    it.boundingbox[0],
                    it.boundingbox[1],
                    it.boundingbox[2],
                    it.boundingbox[3]
                )
        }





}