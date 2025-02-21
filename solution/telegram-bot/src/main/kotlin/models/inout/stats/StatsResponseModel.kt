package ru.cwshbr.models.inout.stats

import kotlinx.serialization.Serializable
import kotlin.math.round

@Serializable
data class StatsResponseModel(
    val impressions_count: Int,
    val click_count: Int,
    val conversion: Float,
    val spent_impressions: Float,
    val spent_clicks: Float,
    val spent_total: Float,
) {
    fun toMessageFormat() : String{
        var out = "📊 *Статистика*\n\n"
        out += "👀 Количество просмотров: $impressions_count\n"
        out += "👆 Количество кликов: $click_count\n"
        out += "📈 Конверсия: ${round(conversion)}%\n"
        out += "🪙 Потрачено на просмотры: ${round(spent_impressions)} и на клики: ${round(spent_clicks)}\n"
        out += "💰 Всего: ${round(spent_total)}"
        return out
    }
}
