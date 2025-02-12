package ru.cwshbr.database.tables

import org.jetbrains.exposed.dao.id.IdTable
import ru.cwshbr.models.enums.Gender
import java.util.UUID

object ClientsTable: IdTable<UUID>("clients") {
    override val id = uuid("id").entityId().uniqueIndex()
    val login = varchar("login", 63).uniqueIndex()
    val age = integer("age")
    val gender = enumerationByName<Gender>("gender", 10)
    val location = varchar("location", 128)
}