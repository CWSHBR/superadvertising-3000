package plugins

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import ru.cwshbr.models.inout.advertisers.AdvertisersRequestResponseModel
import ru.cwshbr.models.inout.advertisers.UpdateMLScoreModel
import ru.cwshbr.models.inout.campaigns.*
import ru.cwshbr.models.inout.clients.ClientResponseRequestModel
import ru.cwshbr.models.inout.time.CurrentTimeModel
import ru.cwshbr.module
import ru.cwshbr.plugins.JsonFormat
import ru.cwshbr.utils.CurrentDate
import java.util.*
import kotlin.collections.List
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAdsTest {

    val locations = listOf("москва", "казань", "набережные челны", "самара", "сочи", "елабуга")
    val ages = 5..50
    val genders = listOf("MALE", "FEMALE")

    val agesForCampaigns = listOf(5, 10, 15, 20, 25, null)
    val gendersForCampaign = genders + listOf("ALL", null)
    val locationsForCampaign = locations + listOf(null)

    val clients = IntRange(0, 49).map { ClientResponseRequestModel(
        UUID.randomUUID().toString(),
        randomString(20),
        ages.random(),
        locations.random(),
        genders.random()
    ) }

    val advs = IntRange(0, 8).map { AdvertisersRequestResponseModel(
        UUID.randomUUID().toString(),
        randomString(15),
    )
    }


    private fun randomString(length: Int): String {
        val symbols = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        var out = ""
        repeat(length){
            out += symbols.random()
        }
        return out
    }

    @Test
    fun setMlShowAdSetClick() = testApplication {
        application {
            module()
        }

        var endpoint = "/clients/bulk"

        assertEquals(HttpStatusCode.Created, client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(clients))
        }.status)

        endpoint = "/advertisers/bulk"

        assertEquals(HttpStatusCode.Created, client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(advs))
        }.status)

        advs.forEach { adv ->
            var endpoint = "/advertisers/bulk"

            var r = client.post(endpoint){
                contentType(ContentType.Application.Json)
                setBody(JsonFormat.encodeToString(listOf(adv)))
            }

            assertEquals(HttpStatusCode.Created, r.status)

            endpoint = "/advertisers/${adv.advertiser_id}/campaigns"

            val campaigns = IntRange(0, 49).map {
                val imlim = (5..20).random()
                val cllim = imlim - (imlim * Random.nextFloat()).toInt()

                val imCost = Random.nextFloat()*6
                val clCost = imCost * (1 + Random.nextFloat()) * 3

                val startdate = (2..100).random()
                val enddate = startdate + (10..100).random()

                val agefrom = agesForCampaigns.random()
                var ageto = agesForCampaigns.random()

                if (agefrom != null && ageto != null) ageto += agefrom

                CreateCampaignRequestModel(
                    imlim,
                    cllim,
                    imCost,
                    clCost,
                    randomString(18),
                    randomString(60),
                    startdate,
                    enddate,
                    TargetResponseModel(
                        gendersForCampaign.random(),
                        agefrom,
                        ageto,
                        locationsForCampaign.random()
                    )
                )
            }

            val campaignsGetList = campaigns.map {
                r = client.post(endpoint){
                    contentType(ContentType.Application.Json)
                    setBody(JsonFormat.encodeToString(it))
                }
                assertEquals(HttpStatusCode.Created, r.status)
                JsonFormat.decodeFromString<GetCampaignResponseModel>(r.bodyAsText())
            }
        }

        endpoint = "/ml-scores"

        advs.forEach { adv ->
            clients.forEach { cl ->
                val r = client.post(endpoint){
                    contentType(ContentType.Application.Json)
                    setBody(JsonFormat.encodeToString(UpdateMLScoreModel(
                        cl.client_id,
                        adv.advertiser_id,
                        (0..50).random()
                    )))
                }
                assertEquals(HttpStatusCode.OK, r.status)
            }
        }

        endpoint = "/ads"

        CurrentDate = 4

        repeat(1000) {
            val cl = clients[it%(clients.size)]

            val r = client.get(endpoint) {
                parameter("client_id", cl.client_id)
            }
            if(r.status.value == 200) {
                println("$it $CurrentDate ${r.status.value}")
                val ad = JsonFormat.decodeFromString<GetAdResponseModel>(r.bodyAsText())
                if ((1..4).random() == 4) {

                    if ((1..20).random() == 20) {
                        CurrentDate += 1
                    }

                    val e = endpoint + "/${ad.ad_id}/click"
                    val res = client.post(e) {
                        contentType(ContentType.Application.Json)
                        setBody(
                            JsonFormat.encodeToString(
                                ClickRequestModel(
                                    cl.client_id!!
                                )
                            )
                        )
                    }

                    assertTrue(res.status.toString()) { res.status.value in listOf(204, 429) }
                }
            }
            assertTrue { r.status.value in listOf(200,404) }

            if ((1..20).random() == 20){
                CurrentDate += 1
            }
        }

    }
}