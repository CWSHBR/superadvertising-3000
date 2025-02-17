package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import ru.cwshbr.api.CampaignApi
import ru.cwshbr.keyboards.GetCampaignKeyboards
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.models.inout.SuccessMessage
import ru.cwshbr.utils.MessageReceive
import java.time.LocalDateTime

class CampaignCallbackController(callbackQuery: DataCallbackQuery, bc: BehaviourContext):
    AbstractCallbackAuthController(callbackQuery, bc){
        suspend fun getCampaignCallback() {
            if (advertiserId == null) return

            val campaignId = MessageReceive.getUUIDFromCallback(callbackQuery as DataCallbackQuery, 1)

            val isNoCache = MessageReceive.getStringFromCallback(callbackQuery, 2) == "nocache"

            if (campaignId == null) {
                bc.sendTextMessage(callbackQuery.from.id,
                    text = ErrorMessage.UnknownError.toString()+"\nИли заново получите /mycampaigns",
                    parseMode = MarkdownParseMode)
                return
            }

            val campaign = CampaignApi.getCampaign(advertiserId, campaignId, isNoCache)

            val message = callbackQuery.message as ContentMessage<TextContent>

            if (campaign == null) {
                bc.editMessageText(message,
                    text = ErrorMessage.CampaignNotFound.toString(),
                    replyMarkup = GetCampaignKeyboards.backToMyCampaignsKeyboard(),
                    parseMode = MarkdownParseMode)
                return
            }

            if (isNoCache) {
                bc.editMessageText(message,
                    text = campaign.toMessageFormat() + "\n\n *Получено: ${LocalDateTime.now()}*",
                    replyMarkup = GetCampaignKeyboards.getCampaignKeyboard(campaign),
                    parseMode = MarkdownParseMode
                )
                return
            }

            bc.editMessageText(message,
                text = campaign.toMessageFormat(),
                replyMarkup = GetCampaignKeyboards.getCampaignKeyboard(campaign),
                parseMode = MarkdownParseMode)
        }

    suspend fun getCampaignListCallback() {
        if (advertiserId == null) return

        val campaigns = CampaignApi.getCampaignsList(advertiserId)


        val message = callbackQuery.message as ContentMessage<TextContent>

        if (campaigns.isEmpty()) {
            bc.editMessageText(message, text = SuccessMessage.nothingFoundYet.toString(),
                replyMarkup = null, parseMode = MarkdownParseMode)
            return
        }

        val keys = GetCampaignKeyboards.generateCampaignListKeyboard(campaigns)

        bc.editMessageText(message, text = SuccessMessage.yourCampaigns.toString(), parseMode = MarkdownParseMode,
            replyMarkup = keys
        )

    }
}