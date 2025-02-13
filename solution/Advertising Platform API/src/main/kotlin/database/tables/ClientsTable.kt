package ru.cwshbr.database.tables

import org.jetbrains.exposed.dao.id.IdTable
import ru.cwshbr.models.enums.Gender
import java.util.*

object ClientsTable: IdTable<UUID>("clients") {
    override val id = uuid("id").entityId().uniqueIndex()
    val login = varchar("login", 63).uniqueIndex()
    val age = integer("age")
    val gender = enumerationByName<Gender>("gender", 10)
    val location = varchar("location", 128)
    val latitude = double("latitude").default(0.0)
    val longitude = double("longitude").default(0.0)
}