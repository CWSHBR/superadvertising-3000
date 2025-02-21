package ru.cwshbr.plugins

import dev.inmo.tgbotapi.extensions.api.buildBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.createSubContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onPhoto
import ru.cwshbr.controller.*
import ru.cwshbr.utils.TELEGRAM_BOT_TOKEN

val bot = buildBot(TELEGRAM_BOT_TOKEN){}

suspend fun telegramRoutingInit(){
    bot.buildBehaviourWithLongPolling {
        val subcontext = createSubContext()
        onCommand("login", requireOnlyCommandInMessage = false){
            LoginController(it, subcontext).login()
        }

        onCommand("mycampaigns", requireOnlyCommandInMessage = false){
            CampaignController(it, subcontext).getCampaigns()
        }

        onCommand("stats", requireOnlyCommandInMessage = false){
            StatisticsController(it, subcontext).showStatisticsByAdvertiser()
        }

        onDataCallbackQuery("getcampaign:.*") {
            CampaignCallbackController(it, subcontext).getCampaignCallback()
        }

        onDataCallbackQuery("mycampaigns.*") {
            CampaignCallbackController(it, subcontext).getCampaignListCallback()
        }

        onDataCallbackQuery("getimage:.*") {
            CampaignCallbackController(it, subcontext).getCampaignImage()
        }

        onDataCallbackQuery("setimage:.*") {
            CampaignCallbackController(it, subcontext).setImageThreadStart()
        }

        onDataCallbackQuery("getstats:.*") {
            StatisticsCallbackController(it, subcontext).showStatisticsByCampaign()
        }

        onDataCallbackQuery("gentext:.*") {

        }

        onPhoto {
            CommonController(it, subcontext).onPictureSent()
        }


    }.join()
}