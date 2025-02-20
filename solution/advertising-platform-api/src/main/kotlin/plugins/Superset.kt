package ru.cwshbr.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking
import ru.cwshbr.integrations.superset.SupersetAPI

fun Application.configureSuperset(){
    runBlocking {
        SupersetAPI.loadSupersetDashboardPreset()
    }
}