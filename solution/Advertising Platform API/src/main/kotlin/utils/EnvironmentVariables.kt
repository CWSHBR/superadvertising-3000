package ru.cwshbr.utils

//server vars
val SERVER_PORT = System.getenv("PORT")?.toIntOrNull() ?: 8080

//database vars
val POSTGRES_URL = System.getenv("POSTGRES_URL")
val POSTGRES_USERNAME = System.getenv("POSTGRES_USERNAME")
val POSTGRES_PASSWORD = System.getenv("POSTGRES_PASSWORD")

//Queue
val RABBITMQ_URL = System.getenv("RABBITMQ_URL")
