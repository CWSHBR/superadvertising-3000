package ru.cwshbr.models.inout.campaign

import kotlinx.serialization.Serializable
import ru.cwshbr.models.*
import ru.cwshbr.models.enums.Gender
import java.util.*

@Serializable
data class GetCampaignResponseModel(
    val campaign_id: String,
    val advertiser_id: String,
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
    private fun toGender(gender: String?) = when (gender) {
        "MALE" -> Gender.MALE
        "FEMALE" -> Gender.FEMALE
        "ALL" -> Gender.ALL
        else -> null
    }

    fun toCampaignModel() =
        CampaignModel(
            UUID.fromString(campaign_id),
            UUID.fromString(advertiser_id),
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
