package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import ru.cwshbr.api.AiTextGenerationApi
import ru.cwshbr.keyboards.GetCampaignKeyboards
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.models.inout.SuccessMessage
import ru.cwshbr.utils.MessageReceive

class GenerateTextCallbackController(callbackQuery: DataCallbackQuery, bc: BehaviourContext):
    AbstractCallbackAuthController(callbackQuery, bc) {
        suspend fun generateText(){
            if (advertiserId == null) return

            val campaignId = MessageReceive.getUUIDFromCallback(callbackQuery, 1)

            if (campaignId == null) {
                bc.sendTextMessage(callbackQuery.from.id,
                    text = ErrorMessage.UnknownError.toString(),
                    parseMode = MarkdownParseMode
                )
                return
            }

            val message = callbackQuery.message as ContentMessage<TextContent>

            val text = if (AiTextGenerationApi.generate(advertiserId, campaignId)){
                SuccessMessage.aiSuccess.toString()
            } else {
                ErrorMessage.UnknownError.toString()
            }

            bc.editMessageText(message, text = text,
                replyMarkup = GetCampaignKeyboards.backToCampaignId(campaignId.toString()),
                parseMode = MarkdownParseMode)
        }
}