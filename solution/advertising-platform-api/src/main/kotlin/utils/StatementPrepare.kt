package ru.cwshbr.utils

class StatementPrepare(val start: String) {
    private val stringsToInsert = mutableListOf<String>()
    fun addParam(value: Any?): StatementPrepare {
        stringsToInsert.add(
            when (value){
                is String -> formatString(value)
                is List<*> -> formatList(value)
                else -> value.toString()
        })

        return this
    }

    private fun formatString(value: String) = "'$value'"

    private fun formatList(value: List<*>) = if (value.isEmpty()) formatEmptyList()
    else
        when (value.first()) {
            is String -> formatStringList(value as List<String>)
            is Int -> formatIntList(value as List<Int>)
            is Float -> formatFloatList(value as List<Float>)
            else -> formatEmptyList()
        }


    private fun formatEmptyList() = "ARRAY[]"
    private fun formatStringList(value: List<String>) = "ARRAY[${value.joinToString(",", transform = ::formatString)}]"
    private fun formatIntList(value: List<Int>) = "ARRAY[${value.joinToString(",")}]"
    private fun formatFloatList(value: List<Float>) = "ARRAY[${value.joinToString(",")}]"

    fun build(): String {
        val i = start.count{ it == '?' }
        if (stringsToInsert.size < i){
            throw IllegalArgumentException("You must provide at least $i parameters")
        }

        var t = start

        repeat(i){
            t = t.replaceFirst("?", stringsToInsert[it])
        }

        return t

    }
}