package ru.cwshbr.database.tables

import org.jetbrains.exposed.sql.Table

object Clicks: Table("clicks") {
    val campaignId = uuid("campaign_id")
    val userId = uuid("user_id")
    val cost = float("cost")
    val date = integer("date")
}