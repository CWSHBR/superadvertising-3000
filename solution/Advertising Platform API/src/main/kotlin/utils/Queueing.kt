package ru.cwshbr.utils

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.ChannelContext
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.basicPublish
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.cwshbr.database.crud.ClientsCRUD
import ru.cwshbr.integrations.nominatim.Nominatim
import ru.cwshbr.models.integrations.nominatim.Position
import ru.cwshbr.models.rabbitmq.LocationMessageModel
import ru.cwshbr.plugins.rabbitMQChannel
import ru.cwshbr.plugins.rabbitMQConnMan
import java.util.*

object Queueing {
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

    suspend fun consumeLocationMessage(message: String): Boolean {
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
}