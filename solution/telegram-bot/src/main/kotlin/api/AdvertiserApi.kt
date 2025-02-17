package ru.cwshbr.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import ru.cwshbr.models.inout.advertiser.AdvertisersResponseModel
import ru.cwshbr.plugins.client
import ru.cwshbr.utils.BASE_URL
import java.util.*

object AdvertiserApi {
    suspend fun getAdvertiserName(id: UUID): String? {
        val response = client.get("$BASE_URL/advertisers/$id")

        if (response.status.value == 200) {
            val r = response.body<AdvertisersResponseModel>()
            return r.name
        }

        return null
    }

}