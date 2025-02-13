package ru.cwshbr.database.crud

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import ru.cwshbr.database.tables.CampaignTargetTable
import ru.cwshbr.database.tables.CampaignsTable
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.CampaignTarget
import java.util.*

object CampaignsCRUD {
    fun resultRowToCampaign(resultRow: ResultRow) =
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
        try{
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
                it[gender] = campaign.target.gender
                it[ageFrom] = campaign.target.ageFrom
                it[ageTo] = campaign.target.ageTo
                it[location] = campaign.target.location
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun read(campaignId: UUID) = transaction {
        CampaignsTable.join(CampaignTargetTable, JoinType.INNER,
            (CampaignsTable.id eq CampaignTargetTable.campaignId))
            .selectAll()
            .where { CampaignsTable.id eq campaignId }
            .singleOrNull()
            .let { if (it == null) null
            else resultRowToCampaign(it) }
    }

    fun readByAdvertiserId(advertiserId: UUID, size: Int, page: Int) = transaction {
        CampaignsTable.join(CampaignTargetTable, JoinType.INNER,
            (CampaignsTable.id eq CampaignTargetTable.campaignId))
            .selectAll()
            .where { CampaignsTable.advertiserId eq advertiserId }
            .limit(size).offset(size*page.toLong())
            .map (::resultRowToCampaign)
    }

    //todo UPDATE

    fun delete(campaignId: UUID) = transaction {
        CampaignsTable.deleteWhere { CampaignsTable.id eq campaignId }
    }
}