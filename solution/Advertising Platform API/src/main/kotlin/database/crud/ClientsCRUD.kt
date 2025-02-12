package ru.cwshbr.database.crud

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.cwshbr.database.tables.ClientsTable
import ru.cwshbr.models.ClientModel
import java.util.UUID

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
        }
    }

    fun createList(clients: List<ClientModel>): Pair<Boolean, String?> {
        try {
            transaction {
                clients.forEach { client: ClientModel ->
                    ClientsTable.insert {
                        it[id] = client.id
                        it[login] = client.login
                        it[age] = client.age
                        it[gender] = client.gender
                        it[location] = client.location
                    }
                }
            }
        } catch (ex: ExposedSQLException) {
            return Pair(false, ex.cause?.message)
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