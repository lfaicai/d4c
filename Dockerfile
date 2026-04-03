# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package \
    && JAR=$(find /app/target -maxdepth 1 -name "*.jar" ! -name "*-plain.jar" | head -n1) \
    && cp "$JAR" /app/app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build /app/app.jar app.jar

EXPOSE 44444
ENTRYPOINT ["java", "-jar", "app.jar"]
