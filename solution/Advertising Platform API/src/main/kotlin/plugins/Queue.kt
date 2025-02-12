package ru.cwshbr.plugins

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.ktor.server.application.*
import ru.cwshbr.utils.Queueing
import ru.cwshbr.utils.RABBITMQ_URL

var rabbitMQChannel: Channel? = null
var rabbitMQConnMan: ConnectionManager? = null

fun Application.configureRabbitMQ() {
    println(RABBITMQ_URL)

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

        connection(id = "consume-nom") {
            basicConsume {
                autoAck = true
                queue = "nominatim"
                deliverCallback<String> { tag, message ->
                    println("$tag -> $message")
                    Queueing.consumeLocationMessage(message)
                }
            }
        }
        rabbitMQConnMan = connectionManager
        rabbitMQChannel = channel

    }
}