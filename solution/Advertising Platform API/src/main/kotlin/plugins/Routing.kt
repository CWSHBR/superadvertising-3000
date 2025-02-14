package ru.cwshbr.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.cwshbr.controller.AdvertiserController
import ru.cwshbr.controller.CampaignController
import ru.cwshbr.controller.ClientController
import ru.cwshbr.controller.TimeController

fun Application.configureRouting() {
    routing {
        route("/api") {
            post("/time/advance"){
                TimeController(call).incrementTime()
            }

            route("/clients"){
                post("/bulk"){
                    ClientController(call).createClients()
                }
                get("/{clientId}"){
                    ClientController(call).getClientById()
                }
            }

            route("/advertisers"){
                route("/{advertiserId}"){
                    get{
                        AdvertiserController(call).getAdvertiser()
                    }

                    route("/campaigns"){
                        post {
                            CampaignController(call).createCampaign()
                        }
                        get {
                            CampaignController(call).getListOfCampaigns()
                        }

                        route("/{campaignId}"){
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

            post("/ml-scores"){
                AdvertiserController(call).updateMlScore()
            }
        }
    }
}
