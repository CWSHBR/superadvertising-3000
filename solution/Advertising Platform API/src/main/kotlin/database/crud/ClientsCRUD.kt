package ru.cwshbr.database.crud

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.cwshbr.database.tables.ClientsTable
import ru.cwshbr.models.ClientModel
import ru.cwshbr.models.integrations.nominatim.Position
import java.util.*

object ClientsCRUD {
    private fun resultRowToClient(resultRow: ResultRow) =
        ClientModel(
            resultRow[ClientsTable.id].value,
            resultRow[ClientsTable.login],
            resultRow[ClientsTable.age],
            resultRow[ClientsTable.location],
            resultRow[ClientsTable.gender]
        )

    fun create(client: ClientModel) = transaction {
        ClientsTable.insert {
            it[id] = client.id
            it[login] = client.login
            it[age] = client.age
            it[gender] = client.gender
            it[location] = client.location
            it[latitude] = 0.0
            it[longitude] = 0.0
        }
    }

    fun addPosition(poses: Pair<UUID, Position>) = transaction {
        try {
            poses.let { pair ->
                val (id, pos) = pair
                ClientsTable.update ({ ClientsTable.id eq id }) {
                    it[latitude] = pos.latitude
                    it[longitude] = pos.longitude
                }
            }
        } catch (_: Exception) { }

    }

    fun clientExists(id: UUID) = transaction {
        ClientsTable.selectAll()
            .where { ClientsTable.id eq id }
            .count() > 0
    }

    fun createOrUpdateList(clients: List<ClientModel>): Pair<Boolean, String?> {
        try {
            transaction {
                clients.forEach { client: ClientModel ->
                    if (!clientExists(client.id)) {
                        ClientsTable.insert {
                            it[id] = client.id
                            it[login] = client.login
                            it[age] = client.age
                            it[gender] = client.gender
                            it[location] = client.location
                            it[latitude] = 0.0
                            it[longitude] = 0.0
                        }
                    } else {
                        ClientsTable.update({ ClientsTable.id eq client.id }) {
                            it[login] = client.login
                            it[age] = client.age
                            it[gender] = client.gender
                            it[location] = client.location
                        }
                    }
                }
            }
        } catch (ex: ExposedSQLException) {
            return Pair(false, ex.message)
        }
        return Pair(true, null)

    }

    fun read(id: UUID) = transaction {
        ClientsTable.selectAll()
            .where { ClientsTable.id eq id }
            .singleOrNull()
            .let { if (it == null) null else resultRowToClient(it) }
    }
}