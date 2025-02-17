package ru.cwshbr.utils

import dev.inmo.tgbotapi.types.message.content.TextMessage
import java.util.*

object MessageReceive {
    fun getUUIDFromMessage(m: TextMessage, index: Int = 0): UUID? =
        try {
            UUID.fromString(m.content.text.split(" ")[index])
        } catch (e: Exception){
            null
        }
}