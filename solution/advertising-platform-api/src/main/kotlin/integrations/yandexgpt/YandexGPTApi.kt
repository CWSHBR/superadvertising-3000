package ru.cwshbr.integrations.yandexgpt

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.cwshbr.models.integrations.inout.yandexgpt.getcloud.GetCloudListResponse
import ru.cwshbr.models.integrations.inout.yandexgpt.getfolder.GetFolderListResponse
import ru.cwshbr.models.integrations.inout.yandexgpt.getgpt.*
import ru.cwshbr.models.integrations.inout.yandexgpt.getiam.GetIAMToken
import ru.cwshbr.models.integrations.inout.yandexgpt.getiam.GetIAMTokenResponse
import ru.cwshbr.models.integrations.yandexgpt.CacheUntil
import ru.cwshbr.utils.YANDEX_OAUTH
import ru.cwshbr.utils.client
import java.time.LocalDateTime

object YandexGPTApi {
    private var IAMToken: CacheUntil<String>? = null
    private var folderId: CacheUntil<String>? = null
    private var cloudId: CacheUntil<String>? = null


    suspend fun askGpt(request: String, settings: String): String{
        val iamToken = getIamToken()
        val folderId = getFolder()

        val endpoint = "https://llm.api.cloud.yandex.net/foundationModels/v1/completion"

        val response = client.post(endpoint){
            contentType(ContentType.Application.Json)
            bearerAuth(iamToken)
            setBody(
                AskGptModel(
                    "gpt://$folderId/yandexgpt",
                    GPTCompletionOptions(
                        maxToken = "200",
                        reasoningOptions = ReasoningOptions(
                            "DISABLED"
                        )
                    ),
                    listOf(
                        MessageModel(
                            "system",
                            settings
                        ),
                        MessageModel(
                            "user",
                            request
                        )
                    )

                )
            )
        }

        val body = response.body<AskGptResponse>()

        val answer = body.result.alternatives.first().message.text

        return answer

    }

    private suspend fun getIamToken(): String{
        if (IAMToken != null && IAMToken!!.isValid()) return IAMToken!!.value

        val endpoint = "https://iam.api.cloud.yandex.net/iam/v1/tokens"
        val response = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(
                GetIAMToken(
                    YANDEX_OAUTH
                )
            )
        }

        val body = response.body<GetIAMTokenResponse>()
        val iamToken = body.iamToken

        IAMToken = CacheUntil(iamToken)

        return iamToken
    }

    private suspend fun getCloud(): String {
        if (cloudId != null && cloudId!!.isValid()) return cloudId!!.value

        val iamToken = getIamToken()

        val endpoint = "https://resource-manager.api.cloud.yandex.net/resource-manager/v1/clouds"

        val response = client.get(endpoint){
            bearerAuth(iamToken)
        }

        val body = response.body<GetCloudListResponse>()
        val locCloudId = body.clouds.first().id

        cloudId = CacheUntil(locCloudId, LocalDateTime.now().plusHours(3))

        return locCloudId
    }

    private suspend fun getFolder(): String{
        if (folderId != null && folderId!!.isValid()) return folderId!!.value

        val iamToken = getIamToken()

        val endpoint = "https://resource-manager.api.cloud.yandex.net/resource-manager/v1/folders"

        val response = client.get(endpoint){
            parameter("cloud_id", getCloud())
            bearerAuth(iamToken)
        }

        val body = response.body<GetFolderListResponse>()
        val locFolderId = body.folders.first().id

        folderId = CacheUntil(locFolderId, LocalDateTime.now().plusHours(3))

        return locFolderId

    }
}