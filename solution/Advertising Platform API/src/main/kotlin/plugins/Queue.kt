package ru.cwshbr.plugins

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.exchangeDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueBind
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.queueDeclare
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.rabbitmq
import io.ktor.server.application.*
import ru.cwshbr.utils.RABBITMQ_URL

var rabbitMQChannel: Channel? = null
var rabbitMQConnMan: ConnectionManager? = null

fun Application.configureRabbitMQ() {
    install(RabbitMQ) {
        uri = RABBITMQ_URL
        defaultConnectionName = "def_conn"
        connectionAttempts = 3
        attemptDelay = 2
        dispatcherThreadPollSize = 4
        tlsEnabled = false
    }

    rabbitmq {
        queueBind {
            queue = "nominatim"
            exchange = "exchange"
            routingKey = "to-nominatim"
            queueDeclare {
                queue = "nominatim"
                durable = true
            }
            exchangeDeclare {
                exchange = "exchange"
                type = "direct"
            }
        }

        rabbitMQConnMan = connectionManager
        rabbitMQChannel = channel

    }
}
