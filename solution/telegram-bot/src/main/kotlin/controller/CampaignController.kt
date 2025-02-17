package ru.cwshbr.controller

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.types.message.content.TextMessage
import ru.cwshbr.api.CampaignApi

class CampaignController(message: TextMessage, bc: BehaviourContext): AbstractAuthController(message, bc) {
    suspend fun getCampaigns() {
        if (advertiserId == null) return

        val campaigns = CampaignApi.getCampaignsList(advertiserId)

        if (campaigns.isEmpty()) {

        }


    }
}