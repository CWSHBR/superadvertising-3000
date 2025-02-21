package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import ru.cwshbr.api.StatisticApi
import ru.cwshbr.keyboards.GetCampaignKeyboards
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.models.inout.SuccessMessage
import ru.cwshbr.utils.MessageReceive

class StatisticsCallbackController(callbackQuery: DataCallbackQuery, bc: BehaviourContext):
    AbstractCallbackAuthController(callbackQuery, bc) {
    suspend fun showStatisticsByCampaign() {
        if (advertiserId == null) return

        val campaignId = MessageReceive.getUUIDFromCallback(callbackQuery, 1)

        if (campaignId == null) {
            bc.sendTextMessage(callbackQuery.from.id,
                text = ErrorMessage.UnknownError.toString(),
                parseMode = MarkdownParseMode)
            return
        }

        val stats = StatisticApi.getStatsByCampaign(campaignId.toString())
        val message = callbackQuery.message as ContentMessage<TextContent>

        if (stats == null){
            bc.editMessageText(message, text = SuccessMessage.nothingFoundYet.toString(),
                replyMarkup = GetCampaignKeyboards.backToCampaignId(campaignId.toString()),
                parseMode = MarkdownParseMode)
            return
        }

        bc.editMessageText(message, text = stats.toMessageFormat(),
            replyMarkup = GetCampaignKeyboards.backToCampaignId(campaignId.toString()),
            parseMode = MarkdownParseMode)
    }
}