package ru.cwshbr.models.inout.advertisers

import kotlinx.serialization.Serializable
import ru.cwshbr.utils.Validation
import java.util.UUID

@Serializable
data class UpdateMLScoreModel(
    val client_id: String? = null,
    val advertiser_id: String? = null,
    val score: Int? = null
) {
    fun validate(): Boolean {
        val validSet = setOf(
            Validation.validateUUID(client_id),
            Validation.validateUUID(advertiser_id),
            Validation.validateNum(score)
        )

        return false !in validSet
    }

    fun convert(): Triple<UUID, UUID, Int> =
        Triple(
            UUID.fromString(advertiser_id),
            UUID.fromString(client_id),
            score!!
        )
}
