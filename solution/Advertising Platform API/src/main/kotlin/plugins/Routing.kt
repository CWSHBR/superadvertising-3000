package ru.cwshbr.plugins

import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.basicPublish
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.rabbitmq
import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.cwshbr.controller.ClientController
import ru.cwshbr.controller.TimeController

fun Application.configureRouting() {
    routing {
        post("/time/advance"){
            TimeController(call).incrementTime()
        }

        route("/clients"){
            post("/bulk"){
                ClientController(call).createClients()
            }
            get("/{clientId}"){
                ClientController(call).getClientById()
            }
        }

        post("/test"){
            repeat(10) {
                rabbitmq {
                    basicPublish {
                        exchange = "exchange"
                        routingKey = "to-nominatim"
                        message { "Hello World!" }
                    }
                }
            }
        }
    }
}
