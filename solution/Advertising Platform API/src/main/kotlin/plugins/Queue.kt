package ru.cwshbr.plugins

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.ktor.server.application.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cwshbr.database.crud.ClientsCRUD
import ru.cwshbr.integrations.nominatim.Nominatim
import ru.cwshbr.models.integrations.nominatim.Position
import ru.cwshbr.models.rabbitmq.LocationMessageModel
import ru.cwshbr.utils.RABBITMQ_URL
import java.util.*

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
                    consumeLocationMessage(message)
                }
            }
        }
        rabbitMQConnMan = connectionManager
        rabbitMQChannel = channel

    }
}

suspend fun addToLocationQueue(locationMessage: LocationMessageModel): Boolean {
    try {
        println("in addToLocationQueue $locationMessage")

        val data = Json.encodeToString(locationMessage)
        ChannelContext(rabbitMQConnMan!!, rabbitMQChannel!!)
            .basicPublish {
                exchange = "exchange"
                routingKey = "to-nominatim"
                message { data }
            }

    } catch (ex: Exception) {
        println("error in addToLocQueue: ${ex.message}")
        return false
    }
    println("successfully sent to nominatim: $locationMessage")
    return true
}

private suspend fun consumeLocationMessage(message: String): Boolean {
    try {
        println("in consumeLocationMessage: $message")
        val format = Json { encodeDefaults = true }
        val locationMessage = format.decodeFromString<LocationMessageModel>(message)

        if (!locationMessage.isForCampaign){
            val id = UUID.fromString(locationMessage.id)
            val position = Nominatim.getPlacePosition(locationMessage.location) ?: Position(0.0,0.0)
            ClientsCRUD.addPosition(Pair(id, position))

            println("successfully consumed and saved ${locationMessage.id} at $position")
        }
        else {
            //            TODO
        }
        return true
    } catch (e: Exception) {
        println("error in consumeLocationMessage: $e")
        return false
    }
}