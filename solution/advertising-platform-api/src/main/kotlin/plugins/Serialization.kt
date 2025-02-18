package ru.cwshbr.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

val JsonFormat = Json {
    explicitNulls = false
    encodeDefaults = true //кодировка дефолтных значений из дата классов
    isLenient = false
    allowSpecialFloatingPointValues = false
    allowStructuredMapKeys = true
    prettyPrint = false
    useArrayPolymorphism = false
    ignoreUnknownKeys = true
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(JsonFormat)
    }
}
