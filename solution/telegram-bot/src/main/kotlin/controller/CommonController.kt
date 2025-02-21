package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import ru.cwshbr.models.enums.StatusWaitForType
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.states.StateMachine

class CommonController(message: CommonMessage<*>, bc: BehaviourContext):
    AbstractAuthController(message, bc){
        suspend fun onPictureSent(){
            if (advertiserId == null) return

            val status = StateMachine.getStatus(message.from!!.id.chatId.long)

            if (status?.waitFor != StatusWaitForType.Image){
                bc.sendTextMessage(message.from!!.id,
                    text = ErrorMessage.NotExpectedImage.toString(),
                    parseMode = MarkdownParseMode
                )
                return
            }

            when(status.initiator){
                "campaign" -> {
                    CampaignController(message, bc).setImageSent()
                }
                else -> {
                    bc.sendTextMessage(message.from!!.id,
                        text = ErrorMessage.UnknownError.toString(),
                        parseMode = MarkdownParseMode)
                }
            }
        }
}