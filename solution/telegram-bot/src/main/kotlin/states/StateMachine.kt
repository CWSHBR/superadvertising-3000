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
        Caching.status_cache[userId] = statusData
    }

    fun getStatus(userId: Long): StatusData? {
        return if (Caching.status_cache.containsKey(userId)) {
            Caching.status_cache[userId]
        } else {
            null
        }
    }

    fun removeStatus(userId: Long) {
        Caching.status_cache.remove(userId)
    }
}