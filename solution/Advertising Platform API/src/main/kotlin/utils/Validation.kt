package ru.cwshbr.utils

import java.util.*
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

object Validation {
    fun validateField(
        field: String?,
        lengthBounds: IntRange?,
        pattern: String? = null,
        ignoreNull: Boolean = false
    ): Boolean {
        if (field == null) {
            return ignoreNull
        }
        if (lengthBounds != null && field.length !in lengthBounds) {
            return false
        }
        if (pattern != null && !Pattern.matches(pattern, field)) {
            return false
        }
        return true
    }

    fun validateUUID(field: String?, ignoreNull: Boolean = false): Boolean {
        if (field == null) {
            return ignoreNull
        }

        try {
            UUID.fromString(field)
        } catch (e: Exception) {
            return false
        }

        return true

    }

    fun validateEnum(field: String?,
                     enumValues: List<String>,
                     ignoreNull: Boolean = false): Boolean {
        if (field == null) {
            return ignoreNull
        }
        if (field !in enumValues) {
            return false
        }
        return true
    }

    fun validateList(list: List<String>?,
                     lengthStrBounds: IntRange? = null,
                     lengthListBounds: IntRange? = null,
                     ignoreNull: Boolean = false): Boolean {
        if (list == null){
            return ignoreNull
        }

        if (lengthListBounds != null && list.size !in lengthListBounds){
            return false
        }

        if (lengthStrBounds != null && list.isNotEmpty()){
            var minLen = Int.MAX_VALUE
            var maxLen = 0
            list.forEach {
                minLen = min(minLen, it.length)
                maxLen = max(maxLen, it.length)
            }
            if (minLen !in lengthStrBounds || maxLen !in lengthStrBounds){
                return false
            }
        }

        return true

    }

    fun validateNum(num: Int?,
                    bounds: IntRange? = null,
                    ignoreNull: Boolean = false): Boolean {
        if (num == null) {
            return ignoreNull
        }
        if (bounds != null && num !in bounds) {
            return false
        }
        return true
    }
}
