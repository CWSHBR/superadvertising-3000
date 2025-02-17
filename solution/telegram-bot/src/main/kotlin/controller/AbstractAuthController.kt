package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.content.TextMessage
import kotlinx.coroutines.runBlocking
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.states.StateMachine

abstract class AbstractAuthController(val message: TextMessage, val bc: BehaviourContext) {
    val advertiserId = StateMachine.getLogin(message.from!!.id.chatId.long)
    init {
        if (advertiserId == null) {
            runBlocking {
                bc.reply(message, text = ErrorMessage.NotAuthorized.toString(), parseMode = MarkdownParseMode)
            }
        }
    }
}