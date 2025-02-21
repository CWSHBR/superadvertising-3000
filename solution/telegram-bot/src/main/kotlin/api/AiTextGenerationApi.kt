package ru.cwshbr.api

import io.ktor.client.request.*
import ru.cwshbr.plugins.client
import ru.cwshbr.utils.BASE_URL
import java.util.*

object AiTextGenerationApi {
    suspend fun generate(advertiserId: UUID, campaignId: UUID): Boolean{
        val res = client.post("$BASE_URL/advertisers/$advertiserId/campaigns/$campaignId/generatetext")
        return res.status.value == 200
    }
}