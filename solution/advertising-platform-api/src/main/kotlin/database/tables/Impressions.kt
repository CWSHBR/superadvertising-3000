package ru.cwshbr.database.tables

import org.jetbrains.exposed.sql.Table

object Impressions: Table("impressions") {
    val campaignId = uuid("campaign_id")
    val clientId = uuid("client_id")
    val cost = float("cost")
    val date = integer("date")

    init {
        uniqueIndex(campaignId, clientId)
    }
}