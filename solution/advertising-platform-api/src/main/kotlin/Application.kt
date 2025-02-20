package ru.cwshbr

import database.DatabaseFactory
import database.DatabaseInit
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import ru.cwshbr.plugins.configureRabbitMQ
import ru.cwshbr.plugins.configureRouting
import ru.cwshbr.plugins.configureSerialization
import ru.cwshbr.plugins.configureSuperset
import ru.cwshbr.utils.POSTGRES_PASSWORD
import ru.cwshbr.utils.POSTGRES_URL
import ru.cwshbr.utils.POSTGRES_USERNAME
import ru.cwshbr.utils.SERVER_PORT

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}
fun Application.module() {
    while (true) {
        try {
            connect()
            break
        } catch (exception: Exception) {
            println("Нет подключения в БД")
        }
    }
    DatabaseInit.initialize()
    configureSerialization()
//    configureRabbitMQ()
    configureRouting()
    configureSuperset()
}

fun connect() {
        Database.connect(
            DatabaseFactory.createHikariDataSource(
                POSTGRES_URL,
                "org.postgresql.Driver",
                POSTGRES_USERNAME,
                POSTGRES_PASSWORD
            ),
        )
}

