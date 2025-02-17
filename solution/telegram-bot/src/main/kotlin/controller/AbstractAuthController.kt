package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.message.content.TextMessage
import ru.cwshbr.states.StateMachine

abstract class AbstractAuthController(message: TextMessage) {
    val advertiserId = StateMachine.getLogin(message.from!!.id.chatId.long)
}