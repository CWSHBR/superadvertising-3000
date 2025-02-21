package plugins

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.encodeToString
import ru.cwshbr.models.inout.advertisers.AdvertisersRequestResponseModel
import ru.cwshbr.models.inout.campaigns.CreateCampaignRequestModel
import ru.cwshbr.models.inout.campaigns.TargetResponseModel
import ru.cwshbr.models.inout.moderation.ModerationSwitchRequestModel
import ru.cwshbr.module
import ru.cwshbr.plugins.JsonFormat
import kotlin.random.Random
import kotlin.test.Test

class ModerationTest {

    val agesForCampaigns = listOf(5, 10, 15, 20, 25, null)
    val gendersForCampaign = listOf("MALE", "FEMALE", "ALL", null)
    val locationsForCampaign = listOf("москва", "казань", "набережные челны", "самара", "сочи", "елабуга", null)

    private fun randomString(length: Int): String {
        val symbols = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        var out = ""
        repeat(length){
            out += symbols.random()
        }
        return out
    }

    @Test
    fun pretest() = testApplication{
        application {
            module()
        }

        val endpoint = "/moderation"

        var r = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(
                    JsonFormat.encodeToString(
                        ModerationSwitchRequestModel(
                            turn_on = true
                        )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, r.status)

        r = client.post("$endpoint/addrestrictedwords"){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(listOf("xох", "xex")))
        }

        assertEquals(HttpStatusCode.Created, r.status)

        r = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(
                JsonFormat.encodeToString(
                    ModerationSwitchRequestModel(
                        turn_on = false
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, r.status)
    }

    fun createCampaignModel() : CreateCampaignRequestModel {
        val imlim = (2..8).random()
        val cllim = imlim - (imlim * Random.nextFloat()).toInt()

        val imCost = Random.nextFloat()/2f
        val clCost = imCost * (1 + Random.nextFloat()) * 3

        val startdate = (2..6).random()
        val enddate = startdate + (2..6).random()

        val agefrom = agesForCampaigns.random()
        var ageto = agesForCampaigns.random()

        if (agefrom != null && ageto != null) ageto += agefrom

        return CreateCampaignRequestModel(
            imlim,
            cllim,
            imCost,
            clCost,
            randomString(18),
            "Lorem Ipsum - это текст-\"рыба\", часто xох используемый в печати и вэб-дизайне. Lorem Ipsum является стандартной \"рыбой\" для текстов на латинице с начала XVI века.",
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

    @Test
    fun test() = testApplication{
        application {
            module()
        }

        var endpoint = "/advertisers/bulk"
        val adv = AdvertisersRequestResponseModel("6de4d631-d57f-4e83-9341-e2c6b1bc2196", "adv1")

        val res = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(listOf(adv)))
        }

        kotlin.test.assertEquals(HttpStatusCode.Created, res.status)

        endpoint = "/moderation"

        var r = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(
                JsonFormat.encodeToString(
                    ModerationSwitchRequestModel(
                        turn_on = true
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, r.status)

        r = client.post("$endpoint/addrestrictedwords"){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(listOf("xох", "xex")))
        }

        assertEquals(HttpStatusCode.Created, r.status)

        r = client.post("/advertisers/${adv.advertiser_id}/campaigns"){
            contentType(ContentType.Application.Json)
            setBody(JsonFormat.encodeToString(createCampaignModel()))
        }

        assertEquals(HttpStatusCode.BadRequest, r.status)

        r = client.post(endpoint){
            contentType(ContentType.Application.Json)
            setBody(
                JsonFormat.encodeToString(
                    ModerationSwitchRequestModel(
                        turn_on = false
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, r.status)

        r = client.post("/advertisers/${adv.advertiser_id}/campaigns"){
        contentType(ContentType.Application.Json)
        setBody(JsonFormat.encodeToString(createCampaignModel()))
    }

        assertEquals(HttpStatusCode.Created, r.status)

    }

}