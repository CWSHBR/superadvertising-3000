val telegram_bot_api_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.0.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

group = "ru.cwshbr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.cwshbr.MainKt")
}

dependencies {
    //telegram
    implementation("dev.inmo:tgbotapi:$telegram_bot_api_version")
    implementation("dev.inmo:tgbotapi.core:$telegram_bot_api_version")
    implementation("dev.inmo:tgbotapi.api:$telegram_bot_api_version")
    implementation("dev.inmo:tgbotapi.utils:$telegram_bot_api_version")

    //client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    //logging
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

tasks.register("copyDependencies") {
    doLast {
        val libsDir = File("$buildDir/libs/libraries")
        libsDir.mkdirs()

        configurations.getByName("runtimeClasspath").files.forEach {
            if (it.name.endsWith(".jar")) {
                it.copyTo(File(libsDir, it.name))
            }
        }
    }
}