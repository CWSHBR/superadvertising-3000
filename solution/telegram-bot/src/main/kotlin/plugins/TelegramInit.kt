package ru.cwshbr.plugins

import dev.inmo.tgbotapi.extensions.api.buildBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.createSubContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageCallbackQuery
import ru.cwshbr.controller.LoginController
import ru.cwshbr.utils.TELEGRAM_BOT_TOKEN

val bot = buildBot(TELEGRAM_BOT_TOKEN){}

suspend fun telegramRoutingInit(){
    bot.buildBehaviourWithLongPolling {
        val subcontext = createSubContext()
        onCommand("login", requireOnlyCommandInMessage = false){
            LoginController(it, subcontext).login()
        }

        onCommand("mycampaigns", requireOnlyCommandInMessage = false){

        }


    }.join()
}