package ru.cwshbr.utils

import dev.inmo.tgbotapi.types.message.content.TextMessage
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import java.util.*

object MessageReceive {
    fun getUUIDFromMessage(m: TextMessage, index: Int = 0): UUID? =
        try {
            UUID.fromString(m.content.text.split(" ")[index])
        } catch (e: Exception){
            null
        }

    fun getUUIDFromCallback(callback: DataCallbackQuery, index: Int = 1) =
        try {
            UUID.fromString(callback.data.split(":")[index])
        } catch (e: Exception){
            null
        }

    fun getStringFromCallback(callback: DataCallbackQuery, index: Int = 1) =
        try {
            callback.data.split(":")[index]
        } catch (e: Exception){
            null
        }
}