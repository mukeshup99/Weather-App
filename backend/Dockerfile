# Multi-stage build for the Weather-App Spring Boot backend.
#
# Stage 1 ("build") resolves dependencies and packages the jar inside a
# Maven image. We copy pom.xml first and run dependency:go-offline so
# Docker layer caching can reuse the dependency download whenever only
# source files change.
#
# Stage 2 ("runtime") is a slim JRE image carrying just the fat jar.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests \
 && cp target/*.jar /app/app.jar


FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
