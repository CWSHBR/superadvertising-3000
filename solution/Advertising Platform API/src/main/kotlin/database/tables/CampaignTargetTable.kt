package ru.cwshbr.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import ru.cwshbr.models.enums.Gender

object CampaignTargetTable: Table("campaign_target") {
    val campaignId = reference("campaign_id",
        CampaignsTable.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val gender = enumerationByName<Gender>("gender", 10).nullable()
    val ageFrom = integer("age_from").nullable()
    val ageTo = integer("age_to").nullable()
    val location = varchar("location", 128).nullable()
    val latitudeA = double("latitude_a").nullable()
    val latitudeB = double("latitude_b").nullable()
    val longitudeA = double("longitude_a").nullable()
    val longitudeB = double("longitude_b").nullable()
}