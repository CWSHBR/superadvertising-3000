package ru.cwshbr.integrations.superset

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import ru.cwshbr.models.integrations.inout.superset.DashboardListCount
import ru.cwshbr.models.integrations.inout.superset.GetAuthToken
import ru.cwshbr.models.integrations.inout.superset.GetCSRFToken
import ru.cwshbr.models.integrations.inout.superset.LoginModel
import ru.cwshbr.utils.SUPERSET_BASE_URL
import ru.cwshbr.utils.SUPERSET_PASSWORD
import ru.cwshbr.utils.SUPERSET_USERNAME
import ru.cwshbr.utils.client

object SupersetAPI {
    private suspend fun getCSRFToken(accessToken: String): String {
        val response = client.get("$SUPERSET_BASE_URL/security/csrf_token/") {
            bearerAuth(accessToken)
            header(HttpHeaders.UserAgent, "")
        }

        return response.body<GetCSRFToken>().result
    }

    suspend fun checkDashboardExists(): Boolean{
        val response = client.get("$SUPERSET_BASE_URL/dashboard/"){
            bearerAuth(getAccessToken())
        }

        return response.body<DashboardListCount>().count > 0
    }

    suspend fun loadSupersetDashboardPreset(): Boolean{
        val token = getAccessToken()
        val fileBytes = {}.javaClass.classLoader.getResource("NEW export.zip")!!.readBytes()
        client.post("$SUPERSET_BASE_URL/dashboard/import/"){
            setBody(MultiPartFormDataContent(
                formData {
                    append("formData", "NEW export.zip", contentType = ContentType.Application.Zip) {
                        write(fileBytes, 0, fileBytes.size)
                    }
                    append("passwords", "{\"databases/PostgreSQL.yaml\": \"superset\"}")
                    append("overwrite", "true")
                }
            ))
            header(HttpHeaders.ContentType, "multipart/form-data")
            bearerAuth(token)
            contentLength()
        }
        return true
    }

    private suspend fun getAccessToken(): String {
        val response = client.post("$SUPERSET_BASE_URL/security/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginModel(
                SUPERSET_USERNAME,
                SUPERSET_PASSWORD
            ))
        }
        return response.body<GetAuthToken>().access_token
    }
}