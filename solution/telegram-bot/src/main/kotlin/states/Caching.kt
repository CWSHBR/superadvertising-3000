package ru.cwshbr.states

import ru.cwshbr.models.CacheUntil
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.StatusData
import java.util.*

object Caching {
    val statusCache: MutableMap<Long, StatusData> = mutableMapOf()
    val logins = mutableMapOf<Long, UUID>()
    private val campaignsCache = mutableMapOf<UUID, CacheUntil<CampaignModel>>()

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
}