package ru.cwshbr.database.crud

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.cwshbr.database.tables.CampaignsTable
import ru.cwshbr.database.tables.Clicks
import ru.cwshbr.database.tables.Impressions
import ru.cwshbr.models.CampaignModel
import ru.cwshbr.models.StatisticModel
import ru.cwshbr.utils.CurrentDate
import java.util.*

object StatisticsCRUD {
    fun createImpression(campaign: CampaignModel, clientId: UUID) = transaction{
        Impressions.insertIgnore {
            it[Impressions.campaignId] = campaign.id
            it[Impressions.clientId] = clientId
            it[Impressions.cost] = campaign.costPerImpression
            it[Impressions.date] = CurrentDate
        }
    }

    fun impressionExists(campaignId: UUID, clientId: UUID) = transaction{
        Impressions.selectAll()
            .where { (Impressions.campaignId eq campaignId) and (Impressions.clientId eq clientId) }
            .count()
    } > 0

    fun createClick(campaign: CampaignModel, clientId: UUID) = transaction{
        Clicks.insertIgnore {
            it[Clicks.campaignId] = campaign.id
            it[Clicks.clientId] = clientId
            it[Clicks.cost] = campaign.costPerClick
            it[Clicks.date] = CurrentDate
        }
    }

    fun countAllImpressions(campaignId: UUID) = transaction{
        val c = Impressions.date.count().alias("c")
        val s = Impressions.cost.sum().alias("s")
        Impressions.select(c, s)
            .where { Impressions.campaignId eq campaignId }
            .singleOrNull()
            .let { if(it == null) null else
                StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }

    fun countDailyImpressions(campaignId: UUID) = transaction{
        val c = Impressions.date.count().alias("c")
        val s = Impressions.cost.sum().alias("s")
        Impressions.select(Impressions.date, c, s)
            .where { Impressions.campaignId eq campaignId }
            .groupBy(Impressions.date)
            .associate { it[Impressions.date] to StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }

    fun countAllClicks(campaignId: UUID) = transaction{
        val c = Clicks.date.count().alias("c")
        val s = Clicks.cost.sum().alias("s")
        Clicks.select(c, s)
            .where { Clicks.campaignId eq campaignId }
            .singleOrNull()
            .let { if(it == null) null else
                StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }

    fun countDailyClicks(campaignId: UUID) = transaction{
        val c = Clicks.date.count().alias("c")
        val s = Clicks.cost.sum().alias("s")
        Clicks.select(c, s, Clicks.date)
            .where { Clicks.campaignId eq campaignId }
            .groupBy(Clicks.date)
            .associate { it[Clicks.date] to StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }

    fun countAllImpressionsByAdvertiser(advertiserId: UUID) = transaction{
        val c = Impressions.date.count().alias("c")
        val s = Impressions.cost.sum().alias("s")
        Impressions.join(CampaignsTable, JoinType.INNER, Impressions.campaignId, CampaignsTable.id)
            .select(c, s)
            .where { CampaignsTable.advertiserId eq advertiserId }
            .singleOrNull()
            .let { if(it == null) null else
                StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }

    fun countDailyImpressionsByAdvertiser(advertiserId: UUID) = transaction{
        val c = Impressions.date.count().alias("c")
        val s = Impressions.cost.sum().alias("s")
        Impressions.join(CampaignsTable, JoinType.INNER, Impressions.campaignId, CampaignsTable.id)
            .select(c, s, Impressions.date)
            .where { CampaignsTable.advertiserId eq advertiserId }
            .groupBy(Impressions.date)
            .associate { it[Impressions.date] to StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }

    fun countAllClicksByAdvertiser(advertiserId: UUID) = transaction{
        val c = Clicks.date.count().alias("c")
        val s = Clicks.cost.sum().alias("s")
        Clicks.join(CampaignsTable, JoinType.INNER, Clicks.campaignId, CampaignsTable.id)
            .select(c, s)
            .where { CampaignsTable.advertiserId eq advertiserId }
            .singleOrNull()
            .let { if(it == null) null else
                StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }

    fun countDailyClicksByAdvertiser(advertiserId: UUID) = transaction{
        val c = Clicks.date.count().alias("c")
        val s = Clicks.cost.sum().alias("s")
        Clicks.join(CampaignsTable, JoinType.INNER, Clicks.campaignId, CampaignsTable.id)
            .select(c, s, Clicks.date)
            .where { CampaignsTable.advertiserId eq advertiserId }
            .groupBy(Clicks.date)
            .associate { it[Clicks.date] to StatisticModel(it[c].toInt(), it[s] ?: 0f) }
    }


}