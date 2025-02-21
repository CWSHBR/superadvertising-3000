package plugins

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import ru.cwshbr.models.inout.advertisers.AdvertisersRequestResponseModel
import ru.cwshbr.models.inout.campaigns.CreateCampaignRequestModel
import ru.cwshbr.models.inout.campaigns.GetCampaignResponseModel
import ru.cwshbr.models.inout.campaigns.TargetResponseModel
import ru.cwshbr.models.inout.campaigns.UpdateCampaignRequestModel
import ru.cwshbr.models.inout.clients.ClientResponseRequestModel
import ru.cwshbr.models.inout.time.CurrentTimeModel
import ru.cwshbr.module
import ru.cwshbr.plugins.JsonFormat
import ru.cwshbr.utils.CurrentDate
import java.util.UUID
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class RoutingTest {

    val locations = listOf("москва", "казань", "набережные челны", "самара", "сочи", "елабуга")
    val ages = 5..25
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

    val advs = IntRange(0, 49).map { AdvertisersRequestResponseModel(
        UUID.randomUUID().toString(),
        randomString(15),
    )}

    private suspend fun HttpClient.advanceTime(i: Int): HttpResponse{
        val r =this.post("/time/advance") {
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(CurrentTimeModel(i)))
        }
        assertEquals(HttpStatusCode.OK, r.status)
        return r
    }


    private fun randomString(length: Int): String {
        val symbols = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        var out = ""
        repeat(length){
            out += symbols.random()
        }
        return out
    }

    private fun getToCreateCampaign(g: GetCampaignResponseModel) =
        CreateCampaignRequestModel(
            g.impressions_limit,
            g.clicks_limit,
            g.cost_per_impression,
            g.cost_per_click,
            g.ad_title,
            g.ad_text,
            g.start_date,
            g.end_date,
            g.targeting
        )

    @Test
    fun testPostApiClients() = testApplication {
        application {
            module()
        }

        var endpoint = "/clients/bulk"

        //test for bad request
        assertEquals(HttpStatusCode.BadRequest, client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(ClientResponseRequestModel()))
        }.status)


        //actual creating
        assertEquals(HttpStatusCode.Created, client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(clients))
        }.status)

        endpoint = "/clients/"

        //send shitty id
        assertEquals(HttpStatusCode.BadRequest, client.get(endpoint+"adasd").status)

        //send id for client that doesnt exist
        assertEquals(HttpStatusCode.NotFound, client
            .get(endpoint+"00000000-0000-0000-0000-000000000000").status)

        //test for equality between saved client and client that was got
        val savedClient = clients.random()

        val response = client.get(endpoint+savedClient.client_id)

        assertEquals(HttpStatusCode.OK, response.status, "${response.bodyAsText()} ${endpoint+savedClient.client_id}")

        val gottenClient = JsonFormat.decodeFromString<ClientResponseRequestModel>(response.bodyAsText())

        assertEquals(savedClient, gottenClient)

    }

    @Test
    fun testPostApiAdvertisers() = testApplication {
        application {
            module()
        }

        var endpoint = "/advertisers/bulk"

        //test for bad request
        assertEquals(HttpStatusCode.BadRequest, client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(listOf(AdvertisersRequestResponseModel())))
        }.status)


        //actual creating
        assertEquals(HttpStatusCode.Created, client.post(endpoint){
            contentType(ContentType.Application.Json)

            setBody(JsonFormat.encodeToString(advs))
        }.status)

        endpoint = "/advertisers/"

        //send shitty id
        assertEquals(HttpStatusCode.BadRequest, client.get(endpoint+"adasd").status)

        //send id for adv that doesnt exist
        assertEquals(HttpStatusCode.NotFound, client
            .get(endpoint+"00000000-0000-0000-0000-000000000000").status)

        //test for equality between saved client and client that was got
        val savedAdv = advs.random()

        val response = client.get(endpoint+savedAdv.advertiser_id)

        assertEquals(HttpStatusCode.OK, response.status)

        val gottenClient = JsonFormat.decodeFromString<AdvertisersRequestResponseModel>(response.bodyAsText())

        assertEquals(savedAdv, gottenClient)
    }

    @Test
    fun testTimeAdvances() = testApplication {
        application {
            module()
        }
        val i = 1..20

        // check timeadvance
        repeat(5){
            val date = i.random()
            client.advanceTime(date)
            assertEquals(date, CurrentDate)
        }

        CurrentDate = 0
    }

    @Test
    fun createAndGetCampaignTest() = testApplication {
        application {
            module()
        }
        var endpoint = "/advertisers/bulk"
        val adv = advs.random()

        val res = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(listOf(adv)))
        }

        assertEquals(HttpStatusCode.Created, res.status)


        var r = client.post("/advertisers/00000000-0000-0000-0000-000000000000/campaigns")

        assertEquals(HttpStatusCode.NotFound, r.status)

        endpoint = "/advertisers/${adv.advertiser_id}/campaigns"

        val campaigns = IntRange(0, 49).map {
            val imlim = (2..8).random()
            val cllim = imlim - (imlim * Random.nextFloat()).toInt()

            val imCost = Random.nextFloat()/2f
            val clCost = imCost * (1 + Random.nextFloat()) * 3

            val startdate = (2..6).random()
            val enddate = startdate + (2..6).random()

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

        campaigns.forEachIndexed {i, it ->
            val body = JsonFormat.encodeToString(it)
            r = client.post(endpoint){
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            assertEquals(HttpStatusCode.Created, r.status, "$i $body")
        }


        r = client.get(endpoint) {
            contentType(ContentType.Application.Json)
            parameter("size", 50)
            parameter("page", 0)
        }

        val campList = JsonFormat.decodeFromString<List<GetCampaignResponseModel>>(r.bodyAsText())

        assertEquals(HttpStatusCode.OK, r.status)
        assertContentEquals(campaigns, campList.map(::getToCreateCampaign).reversed())

        val camp = campList[0]

        endpoint += "/${camp.campaign_id}"

        r = client.get(endpoint)

        val campaignGet = JsonFormat.decodeFromString<GetCampaignResponseModel>(r.bodyAsText())

        assertEquals(HttpStatusCode.OK, r.status)
        assertEquals(camp, campaignGet)
    }


    @Test
    fun putAndDeleteCampaignTest() = testApplication {
        application {
            module()
        }

        var endpoint = "/advertisers/bulk"
        val adv = advs.random()

        val res = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(listOf(adv)))
        }

        assertEquals(HttpStatusCode.Created, res.status)

        endpoint = "/advertisers/${adv.advertiser_id}/campaigns"

        val imlim = (2..8).random()
        val cllim = imlim - (imlim * Random.nextFloat()).toInt()

        val imCost = Random.nextFloat()/2f
        val clCost = imCost * (1 + Random.nextFloat()) * 3

        val startdate = (2..6).random()
        val enddate = startdate + (2..6).random()

        val agefrom = agesForCampaigns.random()
        var ageto = agesForCampaigns.random()

        if (agefrom != null && ageto != null) ageto += agefrom

        val campaignCreate = CreateCampaignRequestModel(
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

        println("asdasd")

        var r = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(campaignCreate))
        }

        assertEquals(HttpStatusCode.Created, r.status)


        println("asdasd")

        val campaignGet = JsonFormat.decodeFromString<GetCampaignResponseModel>(r.bodyAsText())

        //testing put
        r = client.put("$endpoint/00000000-0000-0000-0000-000000000000"){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(UpdateCampaignRequestModel(
                ad_text = "asdasd"
            )))
        }

        assertEquals(HttpStatusCode.NotFound, r.status)


        endpoint += "/${campaignGet.campaign_id}"

        CurrentDate = (campaignGet.start_date + campaignGet.end_date)/ 2

        println("asdasd")

        r = client.put(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(
                UpdateCampaignRequestModel(
                    impressions_limit = 10
                )
            ))
        }

        assertEquals(HttpStatusCode.BadRequest, r.status)


        r = client.put(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(
                UpdateCampaignRequestModel(
                    start_date = CurrentDate - 1
                )
            ))
        }

        assertEquals(HttpStatusCode.BadRequest, r.status)


        r = client.put(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(
                UpdateCampaignRequestModel(
                    ad_title = randomString(64)
                )
            ))
        }

        assertEquals(HttpStatusCode.BadRequest, r.status)

        r = client.put(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(
                UpdateCampaignRequestModel(
                    ad_text = "adasd"
                )
            ))
        }

        assertEquals(HttpStatusCode.OK, r.status)

        //testing delete
        r = client.delete("/advertisers/${adv.advertiser_id}/campaigns/00000000-0000-0000-0000-000000000000")

        assertEquals(HttpStatusCode.NotFound, r.status)

        r = client.delete(endpoint)

        assertEquals(HttpStatusCode.NoContent, r.status)

        r = client.get(endpoint)
        assertEquals(HttpStatusCode.NotFound, r.status)
    }


}