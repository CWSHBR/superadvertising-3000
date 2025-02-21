package ru.cwshbr.keyboards

import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.inline.dataInlineButton
import ru.cwshbr.models.CampaignModel

object GetCampaignKeyboards {
    private val backToCampaigns = listOf(
        dataInlineButton("⬅\uFE0F Назад к кампаниям", "mycampaigns")
    )

    private fun backToCampaignGen(campaignId: String) = listOf(
        dataInlineButton("⬅\uFE0F Назад к кампании",
            "getcampaign:$campaignId")
    )

    fun campaignListKeyboard(campaigns: List<CampaignModel>) = InlineKeyboardMarkup (
             keyboard = campaigns.map {
                 listOf(
                     dataInlineButton(
                         it.adTitle,
                         "getcampaign:" + it.toKeyboardCallbackFormat()
                     )
                 )
             }
    )

    fun backToMyCampaignsKeyboard() = InlineKeyboardMarkup (
        keyboard = listOf(
            backToCampaigns
        )
    )

    fun backToCampaignId(campaignId: String) = InlineKeyboardMarkup (
        keyboard = listOf(
            backToCampaignGen(campaignId)
        )
    )

    fun getCampaignImage(campaignId: String) = InlineKeyboardMarkup (
        keyboard = listOf(
            listOf(
                dataInlineButton("\uD83D\uDD04\uD83D\uDDBC\uFE0F Изменить картинку", "setimage:$campaignId")
            ),
            backToCampaignGen("$campaignId:recreatemessage")
        )
    )

    fun getCampaignKeyboard(campaign: CampaignModel) = InlineKeyboardMarkup (
        keyboard = listOf(
            listOf(
                dataInlineButton("🔄️ Обновить данные",
                "getcampaign:"+campaign.toKeyboardCallbackFormat()+":nocache"),
                dataInlineButton("📸 Изображение обьявления",
                    "getimage:"+campaign.toKeyboardCallbackFormat())
            ),
            listOf(
                dataInlineButton("\uD83D\uDCCA Статистика",
                    "getstats:"+campaign.toKeyboardCallbackFormat()),
                dataInlineButton("✨ Новый текст с ИИ",
                    "gentext:"+campaign.toKeyboardCallbackFormat()),
            ),
            backToCampaigns
        )
    )


}