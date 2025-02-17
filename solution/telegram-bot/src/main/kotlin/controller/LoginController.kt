package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.content.TextMessage
import ru.cwshbr.api.AdvertiserApi
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.models.inout.SuccessMessage
import ru.cwshbr.states.StateMachine
import ru.cwshbr.utils.MessageReceive

class LoginController(val message: TextMessage, val bc: BehaviourContext) {
    suspend fun login() {
        val advertiserId = MessageReceive.getUUIDFromMessage(message, 1)

        if (advertiserId == null) {
            bc.reply(message, text = ErrorMessage.IncorrectLogin.toString(), parseMode = MarkdownParseMode)
            return
        }

        val name = AdvertiserApi.getAdvertiserName(advertiserId)

        if (name == null){
            bc.reply(message, text = ErrorMessage.AdvertiserNotFound.toString(), parseMode = MarkdownParseMode)
            return
        }

        try {
            StateMachine.setLogin(message.from!!.id.chatId.long, advertiserId)
        } catch (e: Exception) {
            bc.reply(message, text = ErrorMessage.UnknownError.toString(), parseMode = MarkdownParseMode)
            return
        }

        bc.reply(message, text = (SuccessMessage.successfulLogin + "\nВы вошли как `$name`").toString(),
            parseMode = MarkdownParseMode)
    }
}