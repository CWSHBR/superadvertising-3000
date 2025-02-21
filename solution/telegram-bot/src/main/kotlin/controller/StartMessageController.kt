package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.MarkdownParseMode
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage

class StartMessageController(val message: CommonMessage<*>, val bc: BehaviourContext) {
    suspend fun getStartMessage(){
        bc.reply(message, text =
        "👋 Привет! Это бот создан как способ взаимодействия с рекламной платформой\n" +
                "\n" +
                "\uD83E\uDD16 Доступные команды:\n" +
                "- /start - вывод этого сообщения\n" +
                "- /login `{AdvertiserId}` - вход как рекламодатель (замените `{AdvertiserId}` на действительный id)\n" +
                "- /mycampaigns - вывод всех рекламных кампаний\n" +
                "- /stats - вывод статистики по всех кампаниям рекламодателя",
            parseMode = MarkdownParseMode)
    }
}