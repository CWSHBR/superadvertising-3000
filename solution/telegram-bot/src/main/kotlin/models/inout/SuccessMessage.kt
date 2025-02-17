package ru.cwshbr.models.inout

data class SuccessMessage(
    val text: String,
    val emoji: String = "✅"
) {
    companion object {
        val successfulLogin = SuccessMessage("Успешный вход.")
        val nothingFoundYet = SuccessMessage("Тут пока что ничего нет... *Создайте!*", "\uD83E\uDD37")
        val yourCampaigns = SuccessMessage("Ваши рекламные кампании:", "🔍")
    }

    operator fun plus(other: String): SuccessMessage {
        return SuccessMessage("$text $other")
    }

    override fun toString() = "$emoji $text"
}
