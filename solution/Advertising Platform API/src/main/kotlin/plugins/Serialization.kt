package ru.cwshbr.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
            encodeDefaults = true //кодировка дефолтных значений из дата классов
            isLenient = false
            allowSpecialFloatingPointValues = false
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
            ignoreUnknownKeys = true
        })
    }
}
