package ru.cwshbr.database.crud

import database.getBestAdStatement
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.cwshbr.database.tables.CampaignTargetTable
import ru.cwshbr.database.tables.CampaignsTable
import ru.cwshbr.database.tables.Impressions
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.CampaignTarget
import ru.cwshbr.utils.CurrentDate
import ru.cwshbr.utils.StatementPrepare
import java.util.*

object CampaignsCRUD {
    private fun resultRowToCampaign(resultRow: ResultRow) =
        CampaignModel(
            resultRow[CampaignsTable.id].value,
            resultRow[CampaignsTable.advertiserId].value,
            resultRow[CampaignsTable.impressionsLimit],
            resultRow[CampaignsTable.clicksLimit],
            resultRow[CampaignsTable.costPerImpression],
            resultRow[CampaignsTable.costPerClick],
            resultRow[CampaignsTable.adTitle],
            resultRow[CampaignsTable.adText],
            resultRow[CampaignsTable.startDate],
            resultRow[CampaignsTable.endDate],
            CampaignTarget(
                resultRow[CampaignTargetTable.gender],
                resultRow[CampaignTargetTable.ageFrom],
                resultRow[CampaignTargetTable.ageTo],
                resultRow[CampaignTargetTable.location]
            )
        )

    fun create(campaign: CampaignModel) = transaction {
        try {
            CampaignsTable.insert {
                it[id] = campaign.id
                it[advertiserId] = campaign.advertiserId
                it[impressionsLimit] = campaign.impressionsLimit
                it[clicksLimit] = campaign.clicksLimit
                it[costPerImpression] = campaign.costPerImpression
                it[costPerClick] = campaign.costPerClick
                it[adTitle] = campaign.adTitle
                it[adText] = campaign.adText
                it[startDate] = campaign.startDate
                it[endDate] = campaign.endDate
            }

            CampaignTargetTable.insert {
                it[campaignId] = campaign.id
                it[gender] = campaign.target?.gender
                it[ageFrom] = campaign.target?.ageFrom
                it[ageTo] = campaign.target?.ageTo
                it[location] = campaign.target?.location
            }
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, e.message)
        }
    }

    fun read(campaignId: UUID) = transaction {
        (CampaignsTable innerJoin CampaignTargetTable)
            .selectAll()
            .where { CampaignsTable.id eq campaignId }
            .singleOrNull()
            .let {
                if (it == null) null
                else resultRowToCampaign(it)
            }
    }

    fun readByAdvertiserId(advertiserId: UUID, size: Int, page: Int) = transaction {
        (CampaignsTable innerJoin CampaignTargetTable)
            .selectAll()
            .where { CampaignsTable.advertiserId eq advertiserId }
            .limit(size).offset(size * page.toLong())
            .orderBy(CampaignsTable.index to SortOrder.DESC)
            .map(::resultRowToCampaign)
    }

    fun update(campaign: CampaignModel) = transaction {
        try {
            CampaignsTable.update({ CampaignsTable.id eq campaign.id }) {
                it[impressionsLimit] = campaign.impressionsLimit
                it[clicksLimit] = campaign.clicksLimit
                it[costPerImpression] = campaign.costPerImpression
                it[costPerClick] = campaign.costPerClick
                it[adTitle] = campaign.adTitle
                it[adText] = campaign.adText
                it[startDate] = campaign.startDate
                it[endDate] = campaign.endDate
            }

            CampaignTargetTable.update({ CampaignTargetTable.campaignId eq campaign.id }) {
                it[gender] = campaign.target?.gender
                it[ageFrom] = campaign.target?.ageFrom
                it[ageTo] = campaign.target?.ageTo
                it[location] = campaign.target?.location
            }
            Pair(true, null)
        } catch (e: Exception) {
            Pair(true, e.message)
        }
    }
    fun delete(campaignId: UUID) = transaction {
        CampaignsTable.deleteWhere { id eq campaignId }
    }

    fun getAd(clientId: UUID): List<CampaignModel> =
        transaction {
            val user = ClientsCRUD.read(clientId)

            val (keys, vals) = AdvertisersCRUD.getFullMlTable(clientId)

            val statement = StatementPrepare(getBestAdStatement)
                .addParam(keys)
                .addParam(vals)
                .addParam(CurrentDate)
                .addParam(CurrentDate)
                .addParam(user!!.location)
                .addParam(user.age)
                .addParam(user.age)
                .addParam(user.gender.toString())
                .build()

            val i = exec(statement) { rs ->
                val result = arrayListOf<String>()
                while (rs.next()) {
                    result += rs.getString("cid")
                }
                result
            }


            if (i.isNullOrEmpty()) {
                return@transaction listOf()
            } else {
                val ids = i.map { UUID.fromString(it) }.toSet()
                val seenIds = Impressions.select(Impressions.campaignId)
                    .where {Impressions.clientId eq clientId}
                    .map { it[Impressions.campaignId] }.toSet()

                val getIds = (ids - seenIds).toList() //removing ad that client already seen

                println("$clientId ${ids.size} ${seenIds.size} ${getIds.size}")

                return@transaction (CampaignsTable innerJoin CampaignTargetTable)
                    .selectAll()
                    .where { CampaignsTable.id inList getIds }
                    .map(::resultRowToCampaign)
            }


        }
}
