package ru.cwshbr.models.inout

data class SuccessMessage(
    val text: String
) {
    companion object {
        val successfulLogin = SuccessMessage("Успешный вход")
    }

    override fun toString() = "✅ $text"
}
