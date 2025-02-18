package ru.cwshbr.database.crud

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.cwshbr.database.tables.ImagesTable
import java.util.*

object ImagesCRUD {
    fun getImage(id: UUID) = transaction {
        ImagesTable.select(ImagesTable.filename)
            .where {ImagesTable.campaignId eq id }
            .singleOrNull()
            .let { if (it == null) null else it[ImagesTable.filename] }
    }

    fun setImage(id: UUID, newFileName: String) = transaction {
        if (getImage(id) == null) {
            ImagesTable.insert {
                it[filename] = newFileName
                it[campaignId] = id
            }
        }
        else{
            ImagesTable.update({ImagesTable.campaignId eq id}){
                it[filename] = newFileName
            }
        }
    }

}