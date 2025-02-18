package ru.cwshbr.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import ru.cwshbr.plugins.JsonFormat

val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(JsonFormat)
        }
}