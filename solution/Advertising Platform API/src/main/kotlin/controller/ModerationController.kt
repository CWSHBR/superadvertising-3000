package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import ru.cwshbr.models.inout.moderation.ModerationSwitchRequestModel
import ru.cwshbr.utils.Moderation

class ModerationController(val call: ApplicationCall) {
    suspend fun addRestrictedWords() {
        val r = call.receive<List<String>>()
        Moderation.restrictedSet.addAll(r.map { it.trim().lowercase() })
        call.respond(HttpStatusCode.Created)
    }

    suspend fun switchRestrictions() {
        val r = call.receive<ModerationSwitchRequestModel>()
        Moderation.moderationMode = r.turn_on
        call.respond(HttpStatusCode.OK)
    }
}