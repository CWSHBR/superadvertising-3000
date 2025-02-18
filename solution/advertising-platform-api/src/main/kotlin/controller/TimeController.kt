package ru.cwshbr.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.cwshbr.models.inout.time.CurrentTimeModel
import ru.cwshbr.utils.changeDate

class TimeController(val call: ApplicationCall) {
    suspend fun incrementTime(){
        val r = call.receive<CurrentTimeModel>()
        changeDate(r.current_date)
        call.respond(HttpStatusCode.OK, r)
    }
}