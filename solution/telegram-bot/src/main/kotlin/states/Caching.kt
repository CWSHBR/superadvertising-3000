package ru.cwshbr.states

import ru.cwshbr.models.CacheUntil
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.StatusData
import java.time.LocalDateTime
import java.util.*

object Caching {
    val statusCache: MutableMap<Long, StatusData> = mutableMapOf()
    val logins = mutableMapOf<Long, UUID>()
    private val campaignsCache = mutableMapOf<UUID, CacheUntil<CampaignModel>>()
    private val imageCache = mutableMapOf<UUID, CacheUntil<ByteArray>>()

    fun writeNewCampaigns(campaigns: List<CampaignModel>) =
        campaigns.forEach {
            campaignsCache[it.id] = CacheUntil(it)
        }

    fun getCampaign(id: UUID): CampaignModel? {
        val cache = campaignsCache[id]

        if (cache != null && cache.isValid()) {
            return cache.value
        }

        return null
    }

    fun cacheImage(id: UUID, image: ByteArray) {
        imageCache[id] = CacheUntil(image, LocalDateTime.now().plusMinutes(2))
    }

    fun getImage(id: UUID): ByteArray? {
        val cache = imageCache[id]

        if (cache != null && cache.isValid()) {
            return cache.value
        }

        return null
    }
}