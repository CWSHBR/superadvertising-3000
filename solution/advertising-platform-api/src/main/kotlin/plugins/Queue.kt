package ru.cwshbr.plugins

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.ktor.server.application.*
import ru.cwshbr.integrations.yandexgpt.GptQueue
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
            queue = "yandexgpt"
            exchange = "exchange"
            routingKey = "to-yandexgpt"
            queueDeclare {
                queue = "yandexgpt"
                durable = true
            }
            exchangeDeclare {
                exchange = "exchange"
                type = "direct"
            }
        }

        connection(id = "consume-llm") {
            basicConsume {
                autoAck = true
                queue = "yandexgpt"
                deliverCallback<String> { tag, message ->
                    GptQueue.consumeLocationMessage(message)
                }
            }
        }


        rabbitMQConnMan = connectionManager
        rabbitMQChannel = channel

    }
}
