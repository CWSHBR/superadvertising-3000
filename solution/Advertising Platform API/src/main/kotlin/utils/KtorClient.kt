package ru.cwshbr.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


val client = HttpClient(CIO) {
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