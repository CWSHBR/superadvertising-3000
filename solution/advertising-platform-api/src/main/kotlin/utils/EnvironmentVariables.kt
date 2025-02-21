package ru.cwshbr.utils

//server vars
val SERVER_PORT = System.getenv("PORT")?.toIntOrNull() ?: 8080

//database vars
val POSTGRES_URL = System.getenv("POSTGRES_URL")
val POSTGRES_USERNAME = System.getenv("POSTGRES_USERNAME")
val POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD")

//Queue
val RABBITMQ_URL = System.getenv("RABBITMQ_URL")

//s3
val S3_URL = System.getenv("S3_URL")
val S3_ACCESS_KEY = System.getenv("S3_ACCESS_KEY")
val S3_SECRET_KEY = System.getenv("S3_SECRET_KEY")
val S3_BUCKET = System.getenv("S3_BUCKET")

//superset
val SUPERSET_BASE_URL = System.getenv("SUPERSET_BASE_URL")
val SUPERSET_USERNAME = System.getenv("SUPERSET_USERNAME")
val SUPERSET_PASSWORD = System.getenv("SUPERSET_PASSWORD")

//yandex gpt
val YANDEX_OAUTH = System.getenv("YANDEX_OAUTH")
