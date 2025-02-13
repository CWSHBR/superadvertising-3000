package ru.cwshbr.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.cwshbr.controller.AdvertiserController
import ru.cwshbr.controller.ClientController
import ru.cwshbr.controller.TimeController

fun Application.configureRouting() {
    routing {
        route("/api") {
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

            route("/advertisers"){
                get("/{advertiserId}"){
                    AdvertiserController(call).getAdvertiser()
                }

                post("/bulk") {
                    AdvertiserController(call).createAdvertisers()
                }
            }
        }
    }
}
