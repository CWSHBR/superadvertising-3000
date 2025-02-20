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
            CampaignTargetTable,
            Impressions,
            Clicks,
            ImagesTable
        )

    fun initialize() {
        transaction {
            tables.forEach {
                SchemaUtils.create(it)
            }
            exec(createFunctionsStatement)
        }
    }
}

val createFunctionsStatement = """
    CREATE OR REPLACE FUNCTION is_within_area(loc text, clientloc text) returns integer
    as
    ${'$'}${'$'}
    BEGIN
        IF clientLoc is null OR lower(loc) = lower(clientLoc) then
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END;
    ${'$'}${'$'} language plpgsql;

    CREATE OR REPLACE FUNCTION is_age_in_upper_lim(user_age INTEGER, age_limit INTEGER)
        RETURNS INTEGER AS ${'$'}${'$'}
    BEGIN
        IF age_limit is null OR user_age <= age_limit THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END;
    ${'$'}${'$'} LANGUAGE plpgsql;

    CREATE OR REPLACE FUNCTION is_age_in_lower_lim(user_age INTEGER, age_limit INTEGER)
        RETURNS INTEGER AS ${'$'}${'$'}
    BEGIN
        IF age_limit is null OR user_age >= age_limit THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END;
    ${'$'}${'$'} LANGUAGE plpgsql;

    CREATE OR REPLACE FUNCTION is_gender_match(user_gender TEXT, target_gender TEXT)
        RETURNS INTEGER AS ${'$'}${'$'}
    BEGIN
        IF target_gender is null OR target_gender = 'ALL' OR target_gender = user_gender THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END;
    ${'$'}${'$'} LANGUAGE plpgsql;

""".trimIndent()

val getBestAdStatement = """ 
    select cid,
       ((cpi * 0.8 + cpc * 0.4) + ml/2.2) AS score from
    (
        select campaigns.id as cid,
        ms.score as ml, campaigns.cost_per_impression as cpi, campaigns.cost_per_click as cpc from campaigns
        inner join public.campaign_target ct on campaigns.id = ct.campaign_id
        inner join (select * from unnest(?, ?) as x(id, score)) ms on ms.id = cast(advertiser_id as text)

        where (select count(*) from impressions where impressions.campaign_id = campaigns.id) < (campaigns.impressions_limit * 1.05)
                  and campaigns.start_date <= ?
                  and ? <= campaigns.end_date
        and is_within_area(?, ct.location) = 1
        and is_age_in_lower_lim(?, ct.age_from) = 1
        and is_age_in_upper_lim(?, ct.age_to) = 1
        and is_gender_match(?, ct.gender) = 1
    ) as subquery
    order by score DESC, cpi DESC, ml DESC
""".trimIndent()