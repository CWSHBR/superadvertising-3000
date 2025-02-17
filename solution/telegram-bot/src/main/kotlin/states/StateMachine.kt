package ru.cwshbr.states

import ru.cwshbr.models.StatusData
import java.util.*


object StateMachine {
    fun getLogin(userId: Long) =
        Caching.logins[userId]

    fun setLogin(userId: Long, loginData: UUID) {
        Caching.logins[userId] = loginData
    }

    fun setStatus(
        userId: Long,
        statusData: StatusData,
    ) {
        Caching.statusCache[userId] = statusData
    }

    fun getStatus(userId: Long): StatusData? {
        return if (Caching.statusCache.containsKey(userId)) {
            Caching.statusCache[userId]
        } else {
            null
        }
    }

    fun removeStatus(userId: Long) {
        Caching.statusCache.remove(userId)
    }
}