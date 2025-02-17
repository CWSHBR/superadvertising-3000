package ru.cwshbr.models.inout

import ru.cwshbr.api.AdvertiserApi

data class ErrorMessage(
    val text: String
) {
    companion object {
        val IncorrectLogin = ErrorMessage("Неправильный ID.\nПопробуйте `/login {Advertiserid}`")
        val AdvertiserNotFound = ErrorMessage("Такого рекламодателя не существует. Попробуйте другой `Advertiserid`")
        val UnknownError = ErrorMessage("Что-то произошло не так, попробуйте ешё раз.")
        val NotAuthorized = ErrorMessage("Вы не авторизованы.\nПопробуйте `/login {Advertiserid}`")
    }

    override fun toString() = "🚫 $text"

}
