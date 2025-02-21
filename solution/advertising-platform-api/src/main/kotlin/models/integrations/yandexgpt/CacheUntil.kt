package ru.cwshbr.models.integrations.yandexgpt

import java.time.LocalDateTime


data class CacheUntil <T>(
    val value: T,
    val vaildUntil: LocalDateTime = LocalDateTime.now().plusHours(1),
) {
    fun isValid() = vaildUntil.isAfter(LocalDateTime.now())
}
