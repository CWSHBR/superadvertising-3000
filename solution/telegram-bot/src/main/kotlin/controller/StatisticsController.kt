package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import ru.cwshbr.api.StatisticApi
import ru.cwshbr.models.inout.SuccessMessage

class StatisticsController(message: CommonMessage<*>, bc: BehaviourContext): AbstractAuthController(message, bc)  {
    suspend fun showStatisticsByAdvertiser() {
        if (advertiserId == null) return

        val stats = StatisticApi.getStatsByAdvertiser(advertiserId.toString())

        if (stats == null){
            bc.reply(message, text = SuccessMessage.nothingFoundYet.toString(), parseMode = MarkdownParseMode)
            return
        }

        bc.reply(message, text = stats.toMessageFormat(),
            parseMode = MarkdownParseMode)
    }
}