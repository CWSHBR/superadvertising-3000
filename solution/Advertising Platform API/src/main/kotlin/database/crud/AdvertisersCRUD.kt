package ru.cwshbr.database.crud

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.cwshbr.database.tables.AdvertisersTable
import ru.cwshbr.models.AdvertiserModel
import java.util.UUID

object AdvertisersCRUD {
    private fun resultRowToAdvertiser(row: ResultRow) =
        AdvertiserModel(
            row[AdvertisersTable.id].value,
            row[AdvertisersTable.name]
        )

    fun readById(id: UUID) = transaction {
        AdvertisersTable.selectAll()
            .where { AdvertisersTable.id eq id }
            .singleOrNull()
            .let { if (it == null) null
            else resultRowToAdvertiser(it) }
    }

    fun checkExists(id: UUID) = transaction {
        AdvertisersTable.selectAll()
            .where { AdvertisersTable.id eq id }
            .count()
    } > 0

    fun createOrUpdateList(advertisers: List<AdvertiserModel>): Pair<Boolean, String?> {
        try {
           transaction {
               advertisers.forEach { advertiser: AdvertiserModel ->
                   if (!checkExists(advertiser.id)) {
                       AdvertisersTable.insert {
                           it[id] = advertiser.id
                           it[name] = advertiser.name
                       }
                   } else {
                       AdvertisersTable.update({ AdvertisersTable.id eq advertiser.id }) {
                           it[name] = advertiser.name
                       }
                   }
               }
           }
        } catch (e: ExposedSQLException){
            return Pair(false, e.message)
        }

        return Pair(true, null)
    }
}