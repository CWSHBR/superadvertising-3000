package ru.cwshbr.models

import java.time.LocalDateTime


data class CacheUntil <T>(
    val value: T,
    val vaildUntil: LocalDateTime = LocalDateTime.now().plusSeconds(10),
) {
    fun isValid() = vaildUntil.isBefore(LocalDateTime.now())
}
