package ru.cwshbr.models.integrations.inout.yandexgpt.getfolder

import kotlinx.serialization.Serializable

@Serializable
data class GetFolderListResponse(
    val folders: List<GetFolderResponse>
)
