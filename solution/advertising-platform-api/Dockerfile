FROM gradle:8.7.0-jdk as build
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY gradle.properties .
COPY settings.gradle.kts .
COPY src src

#COPY build/libs/libraries libraries

RUN gradle copyDependencies -x test --no-daemon

RUN gradle build -x test --no-daemon --stacktrace

FROM openjdk:21-oracle

WORKDIR /app

COPY --from=build /workspace/app/build/libs/advertising-platform-api-0.0.1.jar ./app.jar
COPY --from=build /workspace/app/build/libs/libraries ./libraries

CMD ["java", "-cp", "libraries/*:app.jar", "ru.cwshbr.ApplicationKt"]