package ru.cwshbr.models

data class StatusData(
    val headMessage: Long,
    val data: MutableList<String>,
) {
    companion object {
        fun valueOf(target: String): StatusData {
            val temp = target.split(":").toMutableList()
            temp.removeAt(0)
            val tempHeadMessage: Long = temp[0].toLong()
            temp.removeAt(0)
            return StatusData(
                tempHeadMessage,
                temp,
            )
        }
    }

    fun asString(): String {
        var stringBuilder: String = ""
        stringBuilder += this.headMessage.toString() + ":"
        this.data.forEach { stringBuilder += "$it:" }
        stringBuilder.slice(0..stringBuilder.length - 2)
        return stringBuilder
    }
}