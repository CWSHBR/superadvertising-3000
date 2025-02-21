package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.delete
import dev.inmo.tgbotapi.extensions.api.edit.text.editMessageText
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.chat.Chat
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import kotlinx.io.Buffer
import ru.cwshbr.api.CampaignApi
import ru.cwshbr.keyboards.GetCampaignKeyboards
import ru.cwshbr.models.StatusData
import ru.cwshbr.models.enums.StatusWaitForType
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.models.inout.SuccessMessage
import ru.cwshbr.states.StateMachine
import ru.cwshbr.utils.MessageReceive
import java.time.LocalDateTime

class CampaignCallbackController(callbackQuery: DataCallbackQuery, bc: BehaviourContext):
    AbstractCallbackAuthController(callbackQuery, bc){

        private suspend fun updateOrCreate(text: String,
                                           replyMarkup: InlineKeyboardMarkup?,
                                           reCreateMessage: Boolean = false): Long{
            val chatId = callbackQuery.message!!.chat as Chat
            val messageId = callbackQuery.message!!.messageId
            if (reCreateMessage) {
                val m = bc.sendMessage(chatId, text = text, replyMarkup = replyMarkup,
                    parseMode = MarkdownParseMode)
                bc.delete(chatId, messageId)
                return m.messageId.long
            } else {
                bc.editMessageText(chatId, messageId, text, replyMarkup = replyMarkup,
                    parseMode = MarkdownParseMode)
                return messageId.long
            }

        }


    suspend fun getCampaignCallback() {
        if (advertiserId == null) return

        val campaignId = MessageReceive.getUUIDFromCallback(callbackQuery as DataCallbackQuery, 1)

        val isNoCache = MessageReceive.getStringFromCallback(callbackQuery, 2) == "nocache"

        val isRecreateMessage = MessageReceive.getStringFromCallback(callbackQuery, 2) == "recreatemessage"

        if (campaignId == null) {
            bc.sendTextMessage(callbackQuery.from.id,
                text = ErrorMessage.UnknownError.toString()+"\nИли заново получите /mycampaigns",
                parseMode = MarkdownParseMode)
            return
        }

        val campaign = CampaignApi.getCampaign(advertiserId, campaignId, isNoCache)

        if (campaign == null) {
            updateOrCreate(text = ErrorMessage.CampaignNotFound.toString(),
                replyMarkup = GetCampaignKeyboards.backToMyCampaignsKeyboard(),
                isRecreateMessage)
            return
        }

        if (isNoCache) {
            updateOrCreate(campaign.toMessageFormat() + "\n\n *Получено: ${LocalDateTime.now()}*",
                GetCampaignKeyboards.getCampaignKeyboard(campaign),
                isRecreateMessage)
            return
        }

        updateOrCreate(campaign.toMessageFormat(),
            GetCampaignKeyboards.getCampaignKeyboard(campaign),
            isRecreateMessage)
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

        val keys = GetCampaignKeyboards.campaignListKeyboard(campaigns)

        bc.editMessageText(message, text = SuccessMessage.yourCampaigns.toString(), parseMode = MarkdownParseMode,
            replyMarkup = keys
        )

    }

    suspend fun getCampaignImage() {
        if (advertiserId == null) return

        val campaignId = MessageReceive.getUUIDFromCallback(callbackQuery as DataCallbackQuery, 1)

        val isNoCache = MessageReceive.getStringFromCallback(callbackQuery, 2) == "nocache"

        if (campaignId == null) {
            bc.sendTextMessage(callbackQuery.from.id,
                text = ErrorMessage.UnknownError.toString(),
                parseMode = MarkdownParseMode)
            return
        }

        val image = CampaignApi.getImage(advertiserId, campaignId, isNoCache)

        val message = callbackQuery.message as ContentMessage<TextContent>

        if (image == null) {
            bc.editMessageText(message, text = SuccessMessage.nothingFoundYet.toString(),
                replyMarkup = GetCampaignKeyboards.getCampaignImage(campaignId.toString()),
                parseMode = MarkdownParseMode)
            return
        }

        val chatId = callbackQuery.message!!.chat as Chat
        val messageId = callbackQuery.message!!.messageId

        val buffer = Buffer()
        buffer.write(image)

        bc.sendPhoto(chatId, InputFile.fromInput("image", inputSource = { buffer }),
            replyMarkup = GetCampaignKeyboards.getCampaignImage(campaignId.toString()))
        bc.delete(chatId, messageId)
    }

    suspend fun setImageThreadStart() {
        if (advertiserId == null) return

        val campaignId = MessageReceive.getUUIDFromCallback(callbackQuery as DataCallbackQuery, 1)

        if (campaignId == null) {
            bc.sendTextMessage(callbackQuery.from.id,
                text = ErrorMessage.UnknownError.toString(),
                parseMode = MarkdownParseMode)
            return
        }


        val id = updateOrCreate(SuccessMessage.sendPicture.toString(),
            null,
            true)

        StateMachine.setStatus(
            callbackQuery.from.id.chatId.long, StatusData(
                id,
                StatusWaitForType.Image,
                "campaign",
                mutableListOf(campaignId.toString())
            ))
    }


}