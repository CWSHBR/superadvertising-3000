package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.files.downloadFile
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.requests.abstracts.FileId
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.content.PhotoMessage
import ru.cwshbr.api.CampaignApi
import ru.cwshbr.keyboards.GetCampaignKeyboards
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.models.inout.SuccessMessage
import ru.cwshbr.states.StateMachine
import java.util.*

class CampaignController(message: CommonMessage<*>, bc: BehaviourContext): AbstractAuthController(message, bc) {
    suspend fun getCampaigns() {
        if (advertiserId == null) return

        val campaigns = CampaignApi.getCampaignsList(advertiserId, size = 25)

        if (campaigns.isEmpty()) {
            bc.reply(message, text = SuccessMessage.nothingFoundYet.toString(), parseMode = MarkdownParseMode)
            return
        }

        val keys = GetCampaignKeyboards.campaignListKeyboard(campaigns)

        bc.reply(message, text = SuccessMessage.yourCampaigns.toString(), parseMode = MarkdownParseMode,
            replyMarkup = keys
        )

    }

    suspend fun setImageSent() {
        if (advertiserId == null) return

        val status = StateMachine.getStatus(message.from!!.id.chatId.long)

        if (status == null){
            bc.sendTextMessage(message.from!!.id,
                text = ErrorMessage.UnknownError.toString(),
                parseMode = MarkdownParseMode)

            return
        }


        try {
            val tgImage = message as PhotoMessage
            val fileId = tgImage.content.asTelegramMedia().file.fileId

            val campaignId = UUID.fromString(status.data[0])

            val imageByteArray = bc.downloadFile(FileId(fileId))

            CampaignApi.setImage(advertiserId, campaignId, imageByteArray)

            StateMachine.removeStatus(message.from!!.id.chatId.long)

            bc.sendPhoto(message.from!!.id, InputFile.fromId(fileId),
                text = SuccessMessage.pictureSaved.toString(),
                parseMode = MarkdownParseMode,
                replyMarkup = GetCampaignKeyboards.getCampaignImage(campaignId.toString()))

        } catch (e: Exception) {
            bc.sendTextMessage(message.from!!.id,
                text = ErrorMessage.UnknownError.toString(),
                parseMode = MarkdownParseMode)
            return
        }
    }
}