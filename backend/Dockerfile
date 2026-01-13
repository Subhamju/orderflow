FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar orderflow-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "orderflow-app.jar"]