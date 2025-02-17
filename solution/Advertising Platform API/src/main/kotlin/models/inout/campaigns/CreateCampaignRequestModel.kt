package ru.cwshbr.models.inout.campaigns

import kotlinx.serialization.Serializable
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.CampaignTarget
import ru.cwshbr.models.enums.Gender
import ru.cwshbr.utils.CurrentDate
import ru.cwshbr.utils.Validation
import java.util.*

@Serializable
data class CreateCampaignRequestModel(
    val impressions_limit: Int,
    val clicks_limit: Int,
    val cost_per_impression: Float,
    val cost_per_click: Float,
    val ad_title: String,
    val ad_text: String,
    val start_date: Int,
    val end_date: Int,
    val targeting: TargetResponseModel
) {
    fun validate(): Boolean {
        val validSet = setOf(
            Validation.validateField(ad_title, 1..63),
            Validation.validateField(ad_text, 1..255),
            Validation.validateEnum(targeting.gender, listOf("MALE", "FEMALE", "ALL"), true),
            Validation.validateField(targeting.location, 1..128, ignoreNull = true),
            CurrentDate <= start_date,
            CurrentDate <= end_date,
            start_date <= end_date
        )

        return false !in validSet
    }

    private fun toGender(gender: String?) = when (gender) {
        "MALE" -> Gender.MALE
        "FEMALE" -> Gender.FEMALE
        "ALL" -> Gender.ALL
        else -> null
    }

    fun toCampaignModel(id: UUID, advertiserId: UUID) =
        CampaignModel(
            id,
            advertiserId,
            impressions_limit,
            clicks_limit,
            cost_per_impression,
            cost_per_click,
            ad_title,
            ad_text,
            start_date,
            end_date,
            CampaignTarget(
                toGender(targeting.gender),
                targeting.age_from,
                targeting.age_to,
                targeting.location
            )
        )
}
