package ru.cwshbr.models


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
    val target: CampaignTarget?
){
    fun toKeyboardCallbackFormat() = "$id"

    fun toMessageFormat() : String{
        var out = ""
        out += "\uD83D\uDCDA Название: $adTitle\n\n"
        out += "\uD83D\uDCC4 Текст: $adText\n\n"
        out += "\uD83C\uDFF7\uFE0F Стоймость просмотра: $costPerImpression и клика: $costPerClick\n"
        out += "\uD83D\uDCC5 C $startDate по $endDate день"
        return out
    }
}
