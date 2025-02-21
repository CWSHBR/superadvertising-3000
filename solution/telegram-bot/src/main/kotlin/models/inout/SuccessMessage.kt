package ru.cwshbr.models.inout

data class SuccessMessage(
    val text: String,
    val emoji: String = "✅"
) {
    companion object {
        val successfulLogin = SuccessMessage("Успешный вход.")
        val nothingFoundYet = SuccessMessage("Тут пока что ничего нет... *Создайте!*", "\uD83E\uDD37")
        val yourCampaigns = SuccessMessage("Ваши рекламные кампании:", "🔍")
        val sendPicture = SuccessMessage("Отправьте изображение для вашей рекламы.",
            "\uD83D\uDCE4\uD83D\uDDBC\uFE0F")
        val pictureSaved = SuccessMessage("Изображение сохранено успешно!")
        val aiSuccess = SuccessMessage("Запрос на генерацию отправлен успешно.\nПодождите немного и обновите страницу с рекламной кампанией!", "✨☑️")
    }

    operator fun plus(other: String): SuccessMessage {
        return SuccessMessage("$text $other")
    }

    override fun toString() = "$emoji $text"
}
