package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.cwshbr.database.crud.ClientsCRUD
import ru.cwshbr.models.inout.ErrorResponse
import ru.cwshbr.models.inout.clients.ClientResponseRequestModel
import java.util.*

class ClientController(val call: ApplicationCall) {
    suspend fun createClients(){
        val r = call.receive<List<ClientResponseRequestModel>>()

        r.forEachIndexed { index, clientio ->
            if (!clientio.validate()) {
                call.respond(HttpStatusCode.BadRequest,
                    ErrorResponse("bad request data in client at $index"))
                return
            }
        }

        val clients = r.map { it.toClientModel() }

        val (success, reason) = ClientsCRUD.createOrUpdateList(clients)

        if (!success){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Unknown client error"))
            return
        }

        call.respond(HttpStatusCode.Created, r)
    }

    suspend fun getClientById(){
        val id = try {
            UUID.fromString(call.parameters["clientId"].toString())
        } catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Bad id"))
            return
        }

        val client = ClientsCRUD.read(id)

        if (client == null){
            call.respond(HttpStatusCode.NotFound, ErrorResponse("Client not found"))
            return
        }

        call.respond(HttpStatusCode.OK, client.toClientResponseModel())
    }
}