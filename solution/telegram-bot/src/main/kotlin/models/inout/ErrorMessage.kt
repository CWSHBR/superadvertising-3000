package ru.cwshbr.models.inout

data class ErrorMessage(
    val text: String
) {
    companion object {
        val IncorrectLogin = ErrorMessage("Неправильный ID.\n Попробуйте `/login {Advertiserid}`")
        val UnknownError = ErrorMessage("Что-то произошло не так, попробуйте ешё раз.")
    }

    override fun toString() = "🚫 $text"

}
