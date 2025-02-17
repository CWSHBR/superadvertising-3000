package ru.cwshbr.states

import ru.cwshbr.models.StatusData
import java.util.*

object Caching {
    val status_cache: MutableMap<Long, StatusData> = mutableMapOf()
    val logins = mutableMapOf<Long, UUID>()
}