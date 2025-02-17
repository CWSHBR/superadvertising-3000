package ru.cwshbr.keyboards

import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.inline.dataInlineButton
import ru.cwshbr.models.CampaignModel

object GetCampaignKeyboards {
    private val backToCampaigns = listOf( dataInlineButton("⬅\uFE0F Назад к кампаниям", "mycampaigns"))

    fun generateCampaignListKeyboard(campaigns: List<CampaignModel>, ) = InlineKeyboardMarkup (
                 keyboard = campaigns.map {  listOf( dataInlineButton(it.adTitle,
                "getcampaign:"+it.toKeyboardCallbackFormat()))
                 }
    )

    fun backToMyCampaignsKeyboard() = InlineKeyboardMarkup (
        keyboard = listOf(backToCampaigns)
    )

    fun getCampaignKeyboard(campaign: CampaignModel) = InlineKeyboardMarkup (
        keyboard = listOf(
            listOf(dataInlineButton("🔄️ Обновить данные",
                "getcampaign:"+campaign.toKeyboardCallbackFormat()+":nocache")),
            backToCampaigns
        )
    )


}