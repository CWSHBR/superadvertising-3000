package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.message
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import kotlinx.coroutines.runBlocking
import ru.cwshbr.models.inout.ErrorMessage
import ru.cwshbr.states.StateMachine

abstract class AbstractCallbackAuthController(val callbackQuery: DataCallbackQuery, val bc: BehaviourContext) {
    val advertiserId = StateMachine.getLogin(callbackQuery.from!!.id.chatId.long)
    init {
        if (advertiserId == null) {
            runBlocking {
                bc.sendTextMessage(callbackQuery.message!!.chat, text = ErrorMessage.NotAuthorized.toString(),
                    parseMode = MarkdownParseMode)
            }
        }
    }
}