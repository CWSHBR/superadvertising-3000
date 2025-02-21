## Подробное описание полей базы данных

```kotlin
// -------- КЛИЕНТЫ ---------------------
object ClientsTable: IdTable<UUID>("clients") {
    override val id = uuid("id").entityId().uniqueIndex()                 // идентификатор клиента (UNIQUE, PK)
    val login = varchar("login", 63).uniqueIndex()                        // логин клиента в системе (UNIQUE)
    val age = integer("age")                                              // возраст
    val gender = enumerationByName<Gender>("gender", 10)                  // пол (MALE, FEMALE)
    val location = varchar("location", 128)                               // локация
}

// -------- РЕКЛАМОДАТЕЛИ ---------------
object AdvertisersTable: IdTable<UUID>("advertisers") {
    override val id = uuid("id").entityId().uniqueIndex()                 // идентификатор рекламодателя (UNIQUE, PK)
    val name = varchar("name", 63)                                        // название рекламодателя
}

// -------- ML SCORE --------------------
object MLScoresTable: Table("ml_scores") {
    val clientId = reference("client_id", ClientsTable.id,                // идентификатор клиента (FK)
        onDelete = ReferenceOption.CASCADE)
    val advertiserId = reference("advertiser_id", AdvertisersTable.id,    // идентификатор рекламодателя (FK)
        onDelete = ReferenceOption.CASCADE)
    val score = integer("score")                                          // параметр mlscore

    init {
        uniqueIndex("client_advertiser_unique", clientId, advertiserId)   // реализация уникальности пар клиент-рекламодель   
    }                                                                     
}

// -------- РЕКЛАМНЫЕ КАМПАНИИ ----------
object CampaignsTable: IdTable<UUID>("campaigns") {
    val index = integer("index").autoIncrement()                          // индекс, используется для сортировки (AI)
    override val id = uuid("id").entityId().uniqueIndex()                 // идентификатор рекламной кампании (UNIQUE, PK)
    val advertiserId = reference("advertiser_id", AdvertisersTable.id,    // идентификатор рекламодателя (FK)
        onDelete = ReferenceOption.CASCADE)
    val impressionsLimit = integer("impressions_limit")                   // лимит просмотров
    val clicksLimit = integer("clicks_limit")                             // лимит кликов
    val costPerImpression = float("cost_per_impression")                  // стоймость просмотра
    val costPerClick = float("cost_per_click")                            // стоймость клика
    val adTitle = varchar("ad_title", 63)                                 // название рекламы
    val adText = text("ad_text")                                          // текст рекламы  
    val startDate = integer("start_date")                                 // начало рекламной кампании
    val endDate = integer("end_date")                                     // конец рекламной кампании
}

// -------- РЕКЛАМНЫЙ ТАРГЕТИНГ ---------
object CampaignTargetTable: Table("campaign_target") {
    val campaignId = reference("campaign_id", CampaignsTable.id,          // идентификатор рекламной кампании (FK)
        onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val gender = enumerationByName<Gender>("gender", 10).nullable()       // пол (MALE, FEMALE, ALL, NULL)
    val ageFrom = integer("age_from").nullable()                          // возраст от * (INT, NULL)
    val ageTo = integer("age_to").nullable()                              // возраст до * (INT, NULL)
    val location = varchar("location", 128).nullable()                    // локация (VARCHAR, NULL)
}

// -------- ПРОСМОТРЫ РЕКЛАМЫ -----------
object Impressions: Table("impressions") {
    val campaignId = uuid("campaign_id")                                  // идентификатор рекламной кампании 
    val clientId = uuid("client_id")                                      // идентификатор клиента 
    val cost = float("cost")                                              // цена просмотра
    val date = integer("date")                                            // день просмотра

    init {
        uniqueIndex(campaignId, clientId)                                 // реализация уникальности пар клиент-реклама
    }
}


// -------- ПЕРЕХОДЫ ПО РЕКЛАМЕ ---------
object Clicks: Table("clicks") {
    val campaignId = uuid("campaign_id")                                  // идентификатор рекламной кампании 
    val clientId = uuid("client_id")                                      // идентификатор клиента 
    val cost = float("cost")                                              // цена просмотра
    val date = integer("date")                                            // день просмотра

    init {
        uniqueIndex(campaignId, clientId)                                 // реализация уникальности пар клиент-рекламодель
    }
}

// -------- РЕКЛАМНЫЕ ИЗОБРАЖЕНИЯ -------
object ImagesTable: Table("images") {
    val campaignId = reference("campaignId", CampaignsTable.id,           // идентификатор рекламной кампании (FK)
        onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val filename = varchar("name", length = 100)                          // название файла из s3 хранилища
}
```

 - Из интересных деталей могу заметить, что в Clicks и Impressions нету прямых рефересов (то есть foreign key) к кампаниям и к клиентам. Реализовано это так дабы исключить изменение статистики, после удаления пользователя или кампании.