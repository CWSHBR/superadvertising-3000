package ru.cwshbr.models

import ru.cwshbr.models.enums.Gender

data class CampaignTarget(
    val gender: Gender?,
    val ageFrom: Int?,
    val ageTo: Int?,
    val location: String?
)
