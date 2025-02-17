package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.buttons.inline.dataInlineButton
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.content.TextMessage
import dev.inmo.tgbotapi.utils.matrix
import dev.inmo.tgbotapi.utils.row
import ru.cwshbr.api.CampaignApi
import ru.cwshbr.keyboards.GetCampaignKeyboards
import ru.cwshbr.models.inout.SuccessMessage

class CampaignController(message: TextMessage, bc: BehaviourContext): AbstractAuthController(message, bc) {
    suspend fun getCampaigns() {
        if (advertiserId == null) return

        val campaigns = CampaignApi.getCampaignsList(advertiserId)

        if (campaigns.isEmpty()) {
            bc.reply(message, text = SuccessMessage.nothingFoundYet.toString(), parseMode = MarkdownParseMode)
            return
        }

        val keys = GetCampaignKeyboards.generateCampaignListKeyboard(campaigns)

        println(bc.reply(message, text = SuccessMessage.yourCampaigns.toString(), parseMode = MarkdownParseMode,
            replyMarkup = keys
        ))

    }
}