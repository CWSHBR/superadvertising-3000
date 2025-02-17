package ru.cwshbr.models.inout

data class ErrorMessage(
    val text: String
) {
    companion object {
        val IncorrectLogin = ErrorMessage("Неправильный ID.\nПопробуйте `/login {Advertiserid}`")
        val AdvertiserNotFound = ErrorMessage("Такого рекламодателя не существует.\nПопробуйте другой `Advertiserid`")
        val UnknownError = ErrorMessage("Что-то произошло не так, попробуйте ешё раз.")
        val NotAuthorized = ErrorMessage("Вы не авторизованы.\nПопробуйте `/login {Advertiserid}`")
        val CampaignNotFound = ErrorMessage("Такой рекламной кампании не существует.")
    }

    override fun toString() = "🚫 $text"

}
