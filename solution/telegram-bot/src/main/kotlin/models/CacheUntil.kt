package ru.cwshbr.models

import java.time.LocalDateTime


data class CacheUntil <T>(
    val value: T,
    val vaildUntil: LocalDateTime = LocalDateTime.now().plusSeconds(60),
) {
    fun isValid() = vaildUntil.isAfter(LocalDateTime.now())
}
