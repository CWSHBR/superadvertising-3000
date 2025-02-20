package ru.cwshbr.models.integrations.inout.superset

import kotlinx.serialization.Serializable

@Serializable
data class DashboardListCount(
    val count: Int
)
