package ru.cwshbr.models.inout

data class SuccessMessage(
    val text: String
) {
    companion object {
        val successfulLogin = SuccessMessage("Успешный вход.")

    }

    operator fun plus(other: String): SuccessMessage {
        return SuccessMessage("$text $other")
    }

    override fun toString() = "✅ $text"
}
