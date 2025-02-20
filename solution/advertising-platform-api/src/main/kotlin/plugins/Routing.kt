package ru.cwshbr.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.cwshbr.controller.*

fun Application.configureRouting() {
    routing {
        post("/time/advance") {
            TimeController(call).incrementTime()
        }

        route("/clients") {
            post("/bulk") {
                ClientController(call).createClients()
            }
            get("/{clientId}") {
                ClientController(call).getClientById()
            }
        }

        route("/advertisers") {
            route("/{advertiserId}") {
                get {
                    AdvertiserController(call).getAdvertiser()
                }

                route("/campaigns") {
                    post {
                        CampaignController(call).createCampaign()
                    }
                    get {
                        CampaignController(call).getListOfCampaigns()
                    }

                    route("/{campaignId}") {
                        route("/image") {
                            get {
                                ImagesController(call).getImage()
                            }
                            post {
                                ImagesController(call).setImage()
                            }
                        }
                        get {
                            CampaignController(call).getCampaignById()
                        }
                        put {
                            CampaignController(call).updateCampaign()
                        }
                        delete {
                            CampaignController(call).deleteCampaign()
                        }
                    }
                }
            }

            post("/bulk") {
                AdvertiserController(call).createAdvertisers()
            }
        }

        post("/ml-scores") {
            AdvertiserController(call).updateMlScore()
        }

        route("/ads") {
            get {
                CampaignController(call).getAd()
            }
            post("/{adId}/click") {
                CampaignController(call).clickAd()
            }
        }

        route("/stats") {
            route("/campaigns/{campaignId}") {
                get {
                    StatisticsController(call).getCampaignStats()
                }
                get("/daily") {
                    StatisticsController(call).getCampaignDailyStats()
                }
            }
            route("/advertisers/{advertiserId}/campaigns") {
                get {
                    StatisticsController(call).getAdvertiserStats()
                }
                get("/daily") {
                    StatisticsController(call).getAdvertiserDailyStats()
                }
            }
        }

        route("/moderation") {
            post("/addrestrictedwords") {
                ModerationController(call).addRestrictedWords()
            }

            post {
                ModerationController(call).switchRestrictions()
            }
        }

    }

}
