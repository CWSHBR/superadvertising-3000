package ru.cwshbr.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ImagesTable: Table("images") {
    val campaignId = reference("campaignId", CampaignsTable.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val filename = varchar("name", length = 100)
}