package ru.cwshbr.models.integrations.inout.superset

import kotlinx.serialization.Serializable

@Serializable
data class LoginModel(
    val username: String,
    val password: String,
    val provider: String = "db",
    val refresh: Boolean = true,
)
