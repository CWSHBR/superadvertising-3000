val exposed_version: String by project
val h2_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgres_version: String by project
val hikaricp_version: String by project


plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.0.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"
}

group = "ru.cwshbr"
version = "0.0.1"

application {
    mainClass.set("ru.cwshbr.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // ktor server
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.github.damirdenis-tudor:ktor-server-rabbitmq:1.3.3")

    // ktor client
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-content-negotiation")

    //s3
    implementation("software.amazon.awssdk:s3:2.20.103")

    //database
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")

    //logs
    implementation("ch.qos.logback:logback-classic:$logback_version")

    //tests
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.0.3")
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