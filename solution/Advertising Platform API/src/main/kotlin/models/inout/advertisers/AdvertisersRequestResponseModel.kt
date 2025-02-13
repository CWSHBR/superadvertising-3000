package ru.cwshbr.models.inout.advertisers

import kotlinx.serialization.Serializable
import ru.cwshbr.models.AdvertiserModel
import ru.cwshbr.utils.Validation
import java.util.*

@Serializable
data class AdvertisersRequestResponseModel(
     val advertiser_id: String? = null,
     val name: String? = null
) {
    fun validate(): Boolean{
        val validSet = setOf(
            Validation.validateUUID(advertiser_id),
            Validation.validateField(name, 1..63)
        )

        return false !in validSet
    }

    fun toAdvertiserModel() =
        AdvertiserModel(
            UUID.fromString(advertiser_id),
            name.toString()
        )
}
