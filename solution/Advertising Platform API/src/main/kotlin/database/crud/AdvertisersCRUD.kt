package ru.cwshbr.database.crud

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.cwshbr.database.tables.AdvertisersTable
import ru.cwshbr.database.tables.MLScoresTable
import ru.cwshbr.models.AdvertiserModel
import java.util.*

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

    fun getMlScore(advertiserId: UUID, clientId: UUID)= transaction {
        MLScoresTable.select(MLScoresTable.score)
            .where { (MLScoresTable.advertiserId eq advertiserId) and
                    (MLScoresTable.clientId eq clientId) }
            .singleOrNull()
            .let { if (it == null) null else it[MLScoresTable.score] }
    }

    fun updateMlScore(advertiserId: UUID,
                      clientId: UUID,
                      score: Int): Pair<Boolean, String?>  {
        try {
            transaction {
                if (getMlScore(advertiserId, clientId) == null) {
                    MLScoresTable.insert {
                        it[MLScoresTable.advertiserId] = advertiserId
                        it[MLScoresTable.clientId] = clientId
                        it[MLScoresTable.score] = score
                    }
                } else {
                    MLScoresTable.update({ (MLScoresTable.advertiserId eq advertiserId) and
                            (MLScoresTable.clientId eq clientId) }) {
                        it[MLScoresTable.score] = score
                    }
                }
            }
        } catch (e: ExposedSQLException){
            return Pair(false, e.message)
        }

        return Pair(true, null)

    }
}