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
            Clicks
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
    CREATE OR REPLACE FUNCTION is_within_area(user_lat DOUBLE PRECISION, user_lon DOUBLE PRECISION, latA DOUBLE PRECISION, latB DOUBLE PRECISION, lonA DOUBLE PRECISION, lonB DOUBLE PRECISION)
        RETURNS INTEGER AS ${'$'}${'$'}
    BEGIN
        IF user_lat BETWEEN latA AND latB AND user_lon BETWEEN lonA AND lonB THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END;
    ${'$'}${'$'} LANGUAGE plpgsql;

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
           (0.5+(cpi * 0.5 + cpc * 0.5)) * (0.5+(loc + af + at + gen) /4.0) * (0.5 + ml) AS score from
    (
    select campaigns.id as cid,
           is_within_area(c.latitude, c.longitude,
                               ct.latitude_a, ct.latitude_b, ct.longitude_a, ct.longitude_b) as loc,
        is_age_in_lower_lim(c.age, ct.age_from) as af,
        is_age_in_upper_lim(c.age, ct.age_to) as at,
        is_gender_match(c.gender, ct.gender) as gen,
        ms.score as ml, campaigns.cost_per_impression as cpi, campaigns.cost_per_click as cpc from campaigns
    inner join public.campaign_target ct on campaigns.id = ct.campaign_id
    inner join public.advertisers a on a.id = campaigns.advertiser_id
    inner join public.ml_scores ms on a.id = ms.advertiser_id
    inner join public.clients c on c.id = ms.client_id
    where (select count(*) from impressions where impressions.campaign_id = campaigns.id) < campaigns.impressions_limit
    and (select count(*) from clicks where clicks.campaign_id = campaigns.id) <= campaigns.clicks_limit
    and campaigns.start_date <= %d
    and %d <= campaigns.end_date
    and c.id = '%S'
    ) as subquery
     order by score DESC, ml DESC, cpi DESC limit 1
""".trimIndent()