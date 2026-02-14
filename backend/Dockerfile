# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar orderflow-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "orderflow-app.jar"]