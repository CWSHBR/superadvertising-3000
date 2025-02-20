package ru.cwshbr.integrations.superset

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ru.cwshbr.models.integrations.inout.superset.DashboardListCount
import ru.cwshbr.models.integrations.inout.superset.GetAuthToken
import ru.cwshbr.models.integrations.inout.superset.GetCSRFToken
import ru.cwshbr.models.integrations.inout.superset.LoginModel
import ru.cwshbr.utils.SUPERSET_BASE_URL
import ru.cwshbr.utils.SUPERSET_PASSWORD
import ru.cwshbr.utils.SUPERSET_USERNAME
import ru.cwshbr.utils.client
import java.io.File
import kotlin.io.path.fileVisitor

object SupersetAPI {
    private var authToken: String? = null
    private var csrfToken: String? = null

    private suspend fun getCSRFToken(): String {
        if (csrfToken != null) return csrfToken!!

        val response = client.get("$SUPERSET_BASE_URL/security/csrf_token/") {
            bearerAuth(getAccessToken())
        }

        csrfToken = response.body<GetCSRFToken>().result
        return csrfToken!!
    }

    suspend fun checkDashboardExists(): Boolean{
        val response = client.get("$SUPERSET_BASE_URL/dashboard/"){
            bearerAuth(getAccessToken())
        }

        return response.body<DashboardListCount>().count > 0
    }

    suspend fun loadSupersetDashboardPreset(): Boolean{
        val response = client.post("$SUPERSET_BASE_URL/dashboard/preset"){
            bearerAuth(getAccessToken())
            header("X-CSRFToken", getCSRFToken())
            accept(ContentType.Application.Json)
            formData {
                append("formData", File("export-preset.zip").readBytes())
                append("passwords", "{\"databases/PostgreSQL.yaml\":\"$SUPERSET_PASSWORD\"}")
                append("overwrite", true)
            }

        }

        return response.status.value == 200
    }

    private suspend fun getAccessToken(): String {
        if (authToken != null) return authToken!!

        val response = client.post("$SUPERSET_BASE_URL/security/login/") {
            contentType(ContentType.Application.Json)
            setBody(LoginModel(
                SUPERSET_USERNAME,
                SUPERSET_USERNAME
            ))
        }
        authToken = response.body<GetAuthToken>().access_token
        return authToken!!
    }
}