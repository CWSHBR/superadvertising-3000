package ru.cwshbr.models

import ru.cwshbr.models.enums.Gender
import ru.cwshbr.models.inout.clients.ClientResponseRequestModel
import java.util.UUID

data class ClientModel(
    val id: UUID,
    val login: String,
    val age: Int,
    val location: String,
    val gender: Gender
) {
    fun toClientResponseModel() = ClientResponseRequestModel(
        id.toString(),
        login,
        age,
        location,
        gender.toString()
    )
}
