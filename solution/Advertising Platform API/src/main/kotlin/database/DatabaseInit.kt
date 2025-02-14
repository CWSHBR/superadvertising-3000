package database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import ru.cwshbr.database.tables.*

object DatabaseInit {
    private val tables: List<Table> =
        listOf(
            ClientsTable,
            AdvertisersTable,
            MLScoresTable,
            CampaignsTable,
            CampaignTargetTable
        )

    fun initialize() {
        transaction {
            tables.forEach {
                SchemaUtils.create(it)
            }
        }
    }
}