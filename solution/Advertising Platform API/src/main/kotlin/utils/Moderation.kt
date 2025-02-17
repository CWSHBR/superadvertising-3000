package ru.cwshbr.utils

object Moderation {
    val restrictedSet = mutableSetOf<String>()
    var moderationMode = false

    fun checkText(text: String): Boolean {
        if (!moderationMode) return true

        val plaintext = text.lowercase().replace("[^a-zа-яё\\s]".toRegex(), " ")
        val words = plaintext.split(" ").toSet()

        return (words - restrictedSet.toSet()).size >= words.size
    }
}