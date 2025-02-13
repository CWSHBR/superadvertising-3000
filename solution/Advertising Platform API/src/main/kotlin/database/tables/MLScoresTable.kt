package ru.cwshbr.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object MLScoresTable: Table("ml_scores") {
    val clientId = reference("client_id", ClientsTable.id, onDelete = ReferenceOption.CASCADE)
    val advertiserId = reference("advertiser_id", AdvertisersTable.id, onDelete = ReferenceOption.CASCADE)
    val score = integer("score")

    init {
        uniqueIndex("client_advertiser_unique", clientId, advertiserId)
    }
}