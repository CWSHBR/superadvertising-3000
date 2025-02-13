package ru.cwshbr.models

import ru.cwshbr.models.inout.advertisers.AdvertisersRequestResponseModel
import java.util.*

data class AdvertiserModel (
    val id: UUID,
    val name: String
) {
    fun toAdvertiserResponseModel() =
        AdvertisersRequestResponseModel(
            id.toString(),
            name
        )
}