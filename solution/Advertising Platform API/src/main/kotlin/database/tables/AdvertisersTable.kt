package ru.cwshbr.database.tables

import org.jetbrains.exposed.dao.id.IdTable
import java.util.*

object AdvertisersTable: IdTable<UUID>("advertisers") {
    override val id = uuid("id").entityId().uniqueIndex()
    val name = varchar("name", 63)
}