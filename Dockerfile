FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY backend/pom.xml backend/
COPY community-shared/pom.xml community-shared/
COPY community-guide/pom.xml community-guide/
COPY community-gateway/pom.xml community-gateway/

COPY backend/src backend/src
COPY community-shared/src community-shared/src
COPY community-guide/src community-guide/src
COPY community-gateway/src community-gateway/src
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:17.0.16_8-jre-jammy
ARG MODULE
ARG APP
WORKDIR /app
COPY --from=build /src/${MODULE}/target/${APP}-1.0.0.jar /app/service.jar
COPY deploy/healthcheck.sh /app/healthcheck.sh
ENTRYPOINT ["java", "-jar", "/app/service.jar"]
