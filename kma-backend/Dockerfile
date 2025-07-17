# Byggsteget – bygger din Spring Boot-app
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Körsteget – kör din färdigbyggda JAR-fil
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/kma-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]