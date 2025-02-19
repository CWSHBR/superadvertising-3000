package ru.cwshbr.models.inout.campaigns

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.CampaignTarget
import ru.cwshbr.models.enums.Gender
import ru.cwshbr.utils.CurrentDate
import ru.cwshbr.utils.Validation

@Serializable
data class UpdateCampaignRequestModel(
    val impressions_limit: Int? = null,
    val clicks_limit: Int? = null,
    val cost_per_impression: Float? = null,
    val cost_per_click: Float? = null,
    val ad_title: String? = null,
    val ad_text: String? = null,
    val start_date: Int? = null,
    val end_date: Int? = null,
    val targeting: TargetResponseModel? = null
) {
    private fun toGender(gender: String?): Gender? = when (gender) {
        "MALE" -> Gender.MALE
        "FEMALE" -> Gender.FEMALE
        "ALL" -> Gender.ALL
        else -> null
    }

    // hate this pornography but i dont know the other way
    fun toUpdatedModel(oldCampaign: CampaignModel, json: JsonObject): CampaignModel? {
        val il = if (json.containsKey("impressions_limit")) {
            if (impressions_limit == null || oldCampaign.isCampaignStarted())
                return null

            impressions_limit
        } else oldCampaign.impressionsLimit

        val cl = if (json.containsKey("clicks_limit")) {
            if (clicks_limit == null || oldCampaign.isCampaignStarted())
                return null
            clicks_limit
        } else oldCampaign.clicksLimit

        val cpi = if (json.containsKey("cost_per_impression")) {
            if (cost_per_impression == null) return null
            cost_per_impression
        } else oldCampaign.costPerImpression

        val cpc = if (json.containsKey("cost_per_click")) {
            if (cost_per_click == null) return null
            cost_per_click
        } else oldCampaign.costPerClick

        val ati = if (json.containsKey("ad_title")) {
            if (!Validation.validateField(ad_title, 1..63)) return null
            ad_title!!
        } else oldCampaign.adTitle

        val atx = if (json.containsKey("ad_text")) {
            if (!Validation.validateField(ad_text, 1..255)) return null  // todo не проверять текст на длину
            ad_text!!
        } else oldCampaign.adText

        val sd = if (json.containsKey("start_date")) {
            if (start_date == null || oldCampaign.isCampaignStarted() || CurrentDate > start_date) return null
            start_date
        } else oldCampaign.startDate

        val ed = if (json.containsKey("end_date")) {
            if (end_date == null || oldCampaign.isCampaignStarted() || CurrentDate > end_date) return null
            end_date
        } else oldCampaign.endDate

        val t = if (json.containsKey("targeting")) {
            if (targeting == null) null
            else{
                val jsTarget = json["targeting"]!!.jsonObject
                val g = if (jsTarget.containsKey("gender")) toGender(targeting.gender) else oldCampaign.target?.gender
                val af = if (jsTarget.containsKey("age_from")) targeting.age_from else oldCampaign.target?.ageFrom
                val at = if (jsTarget.containsKey("age_to")) targeting.age_to else oldCampaign.target?.ageTo
                val l = if(jsTarget.containsKey("location")) targeting.location else oldCampaign.target?.location

                CampaignTarget(g, af, at, l)
            }

        } else oldCampaign.target

        return CampaignModel(
            oldCampaign.id,
            oldCampaign.advertiserId,
            il,
            cl,
            cpi,
            cpc,
            ati,
            atx,
            sd,
            ed,
            t
        )

    }

}
