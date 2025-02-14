package ru.cwshbr.models

import ru.cwshbr.models.inout.campaigns.GetCampaignResponseModel
import ru.cwshbr.models.inout.campaigns.TargetResponseModel
import ru.cwshbr.utils.CurrentDate
import java.util.UUID

data class CampaignModel(
    val id: UUID,
    val advertiserId: UUID,
    val impressionsLimit: Int,
    val clicksLimit: Int,
    val costPerImpression: Float,
    val costPerClick: Float,
    val adTitle: String,
    val adText: String,
    val startDate: Int,
    val endDate: Int,
    val target: CampaignTarget
) {
    fun toResponseModel() =
        GetCampaignResponseModel(
            id.toString(),
            advertiserId.toString(),
            impressionsLimit,
            clicksLimit,
            costPerImpression,
            costPerClick,
            adTitle,
            adText,
            startDate,
            endDate,
            TargetResponseModel(
                target.gender.toString(),
                target.ageFrom,
                target.ageTo,
                target.location
            )
        )

    fun isCampaignStarted() = CurrentDate >= startDate
}
