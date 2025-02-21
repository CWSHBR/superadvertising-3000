package ru.cwshbr.models

import ru.cwshbr.models.enums.StatusWaitForType

data class StatusData(
    val headMessage: Long,
    val waitFor: StatusWaitForType,
    val initiator: String,
    val data: MutableList<String> = mutableListOf()
)