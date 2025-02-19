package ru.cwshbr.database.tables

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

object CampaignsTable: IdTable<UUID>("campaigns") {
    val index = integer("index").autoIncrement()
    override val id = uuid("id").entityId().uniqueIndex()
    val advertiserId = reference("advertiser_id", AdvertisersTable.id, onDelete = ReferenceOption.CASCADE)
    val impressionsLimit = integer("impressions_limit")
    val clicksLimit = integer("clicks_limit")
    val costPerImpression = float("cost_per_impression")
    val costPerClick = float("cost_per_click")
    val adTitle = varchar("ad_title", 63)
    val adText = varchar("ad_text", 255)
    val startDate = integer("start_date")
    val endDate = integer("end_date")
    val isActive = bool("is_active").default(true)
}