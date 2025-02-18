package ru.cwshbr.models.inout.clients

import kotlinx.serialization.Serializable
import ru.cwshbr.models.ClientModel
import ru.cwshbr.models.enums.Gender
import ru.cwshbr.utils.Validation
import java.util.*

@Serializable
data class ClientResponseRequestModel(
    val client_id: String? = null,
    val login: String? = null,
    val age: Int? = null,
    val location: String? = null,
    val gender: String? = null
) {
    fun validate(): Boolean {
        val validSet = setOf(
            Validation.validateUUID(client_id),
            Validation.validateField(login, 1..63),
            Validation.validateNum(age, 1..100),
            Validation.validateField(location, null),
            Validation.validateEnum(gender, listOf("MALE", "FEMALE"))
        )

        return false !in validSet
    }

    fun toClientModel() =
        ClientModel(
            UUID.fromString(client_id),
            login.toString(),
            age ?: 1,
            location.toString(),
            if (gender == "MALE") Gender.MALE else Gender.FEMALE
        )

}
